package com.cng.desktop.card.io;

import gnu.io.*;
import org.apache.log4j.Logger;
import org.dreamwork.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
    private static final byte[] QUIT   = new byte[0];

    private static final int TIMEOUT = 2000;

    private PacketChecker checker;
    private SerialReadWorker worker;

    public static List<String> getAllSerialPorts () {
        Enumeration en = CommPortIdentifier.getPortIdentifiers ();
        List<String> names = new ArrayList<> ();
        while (en.hasMoreElements ()) {
            CommPortIdentifier id = (CommPortIdentifier) en.nextElement ();
            if (id.getPortType () == CommPortIdentifier.PORT_SERIAL)
                names.add (id.getName ());
        }

        return names;
    }

    public Serial () {}

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

    public Packet read () throws InterruptedException {
        Packet packet = incoming.take ();
        System.out.println ("return a data.");
        return packet;
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
            int pos = 0, max_length = Integer.MAX_VALUE, n;
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            while (running) {
                try {
                    n = in.read ();
                    if (n >= 0) {
                        baos.write (n);
                        if (pos == 4) {
                            max_length = n + 7;         // 5 bytes header, 3 bytes tail
                        } else if (pos >= max_length) {
                            checker.put (baos.toByteArray ());

                            pos = -1;
                            max_length = Integer.MAX_VALUE;
                            baos = new ByteArrayOutputStream ();
                        }
                        pos ++;
                    }
                    sleep (1);
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        }
    }

    private class PacketChecker extends Thread {
        private BlockingQueue<byte[]> packets = new ArrayBlockingQueue<> (16);

        void put (byte[] buff) {
            packets.offer (buff);
        }

        void cancel () {
            packets.offer (QUIT);
        }

        @Override
        public void run () {
            while (true) {
                byte[] buff = packets.poll ();
                if (buff == QUIT) {
                    break;
                }

                if (buff != null) try {
                    Packet packet = decode (buff);
                    if (validate (packet))
                        incoming.offer (packet);
                } catch (IOException ex) {
                    //
                }
            }

            if (logger.isDebugEnabled ()) {
                logger.debug ("packet checker shutdown.");
            }
        }

        private Packet decode (byte[] buff) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream (buff);
            DataInputStream dis = new DataInputStream (bais);
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
            return packet;
        }

        private boolean validate (Packet packet) {
            if (packet.header != 0xCAFE) {
                logger.error ("Invalid packet header: " + String.format ("%04X", packet.header));
                return false;
            }
            if (packet.tail != 0xBABE) {
                logger.error ("Invalid packet tail: " + String.format ("%04X", packet.tail));
                return false;
            }

            int sum = packet.state + packet.type + packet.length;
            for (int i = 0; i < packet.length; i ++) {
                sum += packet.data [i] & 0xff;
            }
            sum &= 0xff;
            if (sum != packet.crc) {
                logger.error (String.format ("Invalid CRC, expect %02X but receive %02X", sum, packet.crc));
                return false;
            }

            return true;
        }
    }
}