import com.cng.desktop.card.io.Command;
import com.cng.desktop.card.io.Packet;
import com.cng.desktop.card.io.Serial;
import com.cng.desktop.card.util.Tools;
import gnu.io.*;
import org.dreamwork.util.StringUtil;

import java.io.*;
import java.util.*;

/**
 * Created by game on 2016/3/7
 */
public class SerialTest {
    public static void main (String[] args) throws Exception {
        final Serial serial = new Serial ("COM6");
        serial.connect ();

        new Thread () {
            @Override
            public void run () {
                Packet packet = serial.read ();
                if (packet != null) {
                    System.out.println (packet);
                }
            }
        }.start ();

        Calendar c = Calendar.getInstance ();
        c.add (Calendar.YEAR, 1);
        long expire = c.getTimeInMillis ();
        Command command = new Command ((short) 1, Command.ACTION_READ, true, System.currentTimeMillis (), expire, 1);
        System.out.println (command);
        System.out.println (Tools.toHex (command.toByteArray ()));
        for (int i = 3; i > 0; i --) {
            System.out.println (i + "...");
            Thread.sleep (1000);
        }
        serial.write (command);
/*
        Enumeration en = CommPortIdentifier.getPortIdentifiers ();
        CommPortIdentifier port;
        Map<String, CommPortIdentifier> map = new HashMap<> ();
        while (en.hasMoreElements ()) {
            port = (CommPortIdentifier) en.nextElement ();
            if (port.getPortType () == CommPortIdentifier.PORT_SERIAL) {
                System.out.println (port.getName ());
                map.put (port.getName (), port);
            }
        }

        port = map.get ("COM6");
        CommPort comm = port.open ("SerialTest", 2000);
        SerialPort serial = (SerialPort) comm;
        serial.setSerialPortParams (9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        final InputStream in = comm.getInputStream ();
        final OutputStream out = comm.getOutputStream ();
        new Thread () {
            @Override
            public void run () {
                System.out.println ("preparing to receive data from serial port.");
                while (true) {
                    int i = 0;
                    try {
                        i = in.read ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                    if (i != -1)
                        System.out.write (i);
                }
            }
        }.start ();



        try {
            byte[] header = {(byte) 0xfe, (byte) 0xca}, tail = {(byte) 0xbe, (byte) 0xba};
            int action = 'R', isAdmin = 1;
            byte[] id = {0x01, 0x00}, timestamp = {4, 3, 2, 1}, expire = {0x0d, 0x0c, 0x0b, 0x0a};
            byte[] card = {(byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa};

            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            baos.write (action);
            baos.write (isAdmin);
            baos.write (id);
            baos.write (timestamp);
            baos.write (expire);
            baos.write (card);

            byte[] body = baos.toByteArray ();
            int sum = 0;
            for (byte b : body) {
                sum += b & 0xff;
            }
            sum &= 0xff;
            baos.write (sum);

            body = baos.toByteArray ();

            System.out.println ("preparing to write data to serial port.");
            for (int i = 3; i > 0; i --) {
                System.out.println (i + "...");
                Thread.sleep (1000);
            }
            System.out.println (StringUtil.format (body));

            out.write (header);
            baos.writeTo (out);
            out.write (tail);
            out.flush ();

        } catch (Exception ex) {
            ex.printStackTrace ();
        }
*/
    }
}
