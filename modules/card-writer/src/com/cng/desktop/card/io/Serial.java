package com.cng.desktop.card.io;

import com.cng.desktop.card.util.Tools;
import gnu.io.*;
import org.apache.log4j.Logger;
import org.dreamwork.util.StringUtil;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by game on 2016/3/8
 */
public class Serial {
    private InputStream in;
    private OutputStream out;
    private SerialPort port;
    private String name;

    private int
            baudRate = 9600,
            dataBits = SerialPort.DATABITS_8,
            stopBits = SerialPort.STOPBITS_1,
            parity   = SerialPort.PARITY_NONE;

    private final BlockingQueue<Packet> incoming = new ArrayBlockingQueue<> (16);
    private static final Logger logger = Logger.getLogger (Serial.class);

    private static final int TIMEOUT = 2000;

    private PacketChecker checker;
    private SerialReadWorker worker;

    public Serial () {

    }

    public Serial (String name) {
        this.name = name;
    }

    public Serial (String name, int baudRate, int dataBits, int stopBits, int parity) {
        this.name = name;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getBaudRate () {
        return baudRate;
    }

    public void setBaudRate (int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits () {
        return dataBits;
    }

    public void setDataBits (int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits () {
        return stopBits;
    }

    public void setStopBits (int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity () {
        return parity;
    }

    public void setParity (int parity) {
        this.parity = parity;
    }

    public void connect () throws Exception {
        CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier (name);
        if (identifier.isCurrentlyOwned ()) {
            throw new IOException ("Can't open " + name);
        }

        CommPort comm = identifier.open (getClass ().getName (), TIMEOUT);
        if (comm instanceof SerialPort) {
            port = (SerialPort) comm;
            port.setSerialPortParams (baudRate, dataBits, stopBits, parity);
            in = port.getInputStream ();
            out = port.getOutputStream ();
            checker = new PacketChecker ();
            worker = new SerialReadWorker ();
            checker.start ();
            worker.start ();
        } else {
            throw new IOException (name + " is not Serial Port!");
        }
    }

    public Packet read (int timeout, TimeUnit unit) throws InterruptedException {
        return incoming.poll (timeout, unit);
    }

    public Packet read () {
        return incoming.poll ();
    }

    public void write (Command command) throws IOException {
        byte[] buff = command.toByteArray ();
        if (logger.isDebugEnabled ()) {
            logger.debug (">>>>>>>>>>>>>>>\r\n" + StringUtil.format (buff) + ">>>>>>>>>>>>>>>");
        }
        out.write (buff);
    }

    public void disconnect () {
        if (worker != null) {
            worker.cancel ();
        }
        if (checker != null) {
            checker.cancel ();
        }
        if (in != null) try {
            in.close ();
        } catch (IOException ex) {
            //
        }
        if (out != null) try {
            out.close ();
        } catch (IOException ex) {
            //
        }
        if (port != null) {
            port.close ();
        }
    }

    private class SerialReadWorker extends Thread {
        boolean running = true;

        private void cancel () {
            running = false;
        }

        @Override
        public void run () {
            while (true) {
                try {
                    int n = in.read ();
//                    if (n == -1) break;
                    if (n != -1)
                        System.out.printf (" %02X", n);
/*
                    int n = in.available ();
                    if (n > 0) {
                        byte[] buff = new byte[n];
                        int length = in.read (buff);
                        System.out.print (Tools.toHex (buff));
//                        System.out.write (buff, 0, length);
                    }
*/
                } catch (IOException e) {
                    e.printStackTrace ();
                }

            }

//            System.out.println ("Kill");
/*
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            DataInputStream dis = new DataInputStream (in);
            while (running) {
                try {
                    Packet packet = new Packet ();
                    packet.header = dis.readUnsignedShort ();
                    packet.state  = dis.read ();
                    packet.type   = dis.read ();
                    packet.length = dis.read ();
                    packet.data   = new byte [packet.length];
                    int length = dis.read (packet.data, 0, packet.length);
                    if (length != packet.data.length) {
                        throw new IOException ("Expect " + packet.length + " bytes, but receive " + length + " bytes.");
                    }
                    packet.crc    = dis.read ();
                    packet.tail   = dis.readUnsignedShort ();
                    checker.put (packet);
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
*/
        }
    }

    private class PacketChecker extends Thread {
        private BlockingQueue<Packet> packets = new ArrayBlockingQueue<> (16);

        void put (Packet packet) {
            packets.offer (packet);
        }

        void cancel () {
            packets.offer (Packet.DISPOSE);
        }

        @Override
        public void run () {
            while (true) {
                Packet packet = packets.poll ();
                if (packet != null) {
                    if (packet == Packet.DISPOSE) {
                        break;
                    }

                    if (packet.header != 0xCAFE) {
                        logger.error ("Invalid packet header: " + String.format ("%04X", packet.header));
                        continue;
                    }
                    if (packet.tail != 0xBABE) {
                        logger.error ("Invalid packet tail: " + String.format ("%04X", packet.tail));
                        continue;
                    }

                    int sum = packet.state + packet.type + packet.length;
                    for (int i = 0; i < packet.length; i ++) {
                        sum += packet.data [i] & 0xff;
                    }
                    sum &= 0xff;
                    if (sum != packet.crc) {
                        logger.error (String.format ("Invalid CRC, expect %02X but receive %02X", sum, packet.crc));
                        continue;
                    }

                    incoming.offer (packet);
                }
            }

            if (logger.isDebugEnabled ()) {
                logger.debug ("packet checker shutdown.");
            }
        }
    }
}