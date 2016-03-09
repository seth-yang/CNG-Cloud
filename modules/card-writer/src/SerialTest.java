import com.cng.desktop.card.data.CardData;
import com.cng.desktop.card.data.CardMark;
import com.cng.desktop.card.io.Command;
import com.cng.desktop.card.io.Packet;
import com.cng.desktop.card.io.Serial;
import com.cng.desktop.card.util.Tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.*;

/**
 * Created by game on 2016/3/7
 */
public class SerialTest {
    public static void main (String[] args) throws Exception {
        final Serial serial = new Serial ("COM6");
        serial.setBaudRate (115200);
        serial.connect ();

        new Thread () {
            @Override
            public void run () {
                while (true) {
                    Packet packet = null;
                    try {
                        packet = serial.read ();
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    if (packet != null) {
                        System.out.println (packet);

                        System.out.println (CardMark.parse (packet.data, 0));
                        System.out.println (CardData.parse (packet.data, 16));
                    }
                }
            }
        }.start ();

        Calendar c = Calendar.getInstance ();
        c.add (Calendar.YEAR, 1);
        long expire = c.getTimeInMillis ();
        Command command = new Command ((short) 1, Command.ACTION_READ, true, System.currentTimeMillis (), expire, 1);
        command.mainVersion = 1;
        command.minVersion = 10;
        System.out.println (command);
        System.out.println (Tools.toHex (command.toByteArray ()));
        for (int j = 0; j < 10; j ++) {
            for (int i = 3; i > 0; i--) {
                System.out.println (i + "...");
                Thread.sleep (1000);
            }
            serial.write (command);
            Thread.sleep (10000);
        }
    }
}
