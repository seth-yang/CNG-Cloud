package com.cng.desktop.card.io;

import com.cng.desktop.card.util.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by game on 2016/3/8
 */
public class Command {
    public static final short header = (short) 0xCAFE, tail = (short) 0xBABE;
    public short id;
    public int action, admin, timestamp, expire, cardNo, mainVersion = 1, minVersion = 0;

    public static final int ACTION_WRITE = 'W', ACTION_READ = 'R';

    public Command () {}

    public Command (short id, int action, boolean admin, long timestamp, long expire, int cardNo) {
        this.id = id;
        this.action = action;
        this.admin = (admin) ? 1 : 0;
        this.timestamp = (int) (timestamp / 1000);
        this.expire = (int) (expire / 1000);
        this.cardNo = cardNo;
    }

    @Override
    public String toString () {
        return "Command - {\r\n" +
                "\theader    : " + String.format ("0x%04X", header) + "\r\n" +
                "\taction    : " + String.format ("0x%02X", action) + "\r\n" +
                "\tadmin     : " + (admin != 0) + "\r\n" +
                "\tid        : " + String.format ("0x%04X", id) + "\r\n" +
                "\ttimestamp : " + Tools.toHex (Tools.intToBytes (timestamp)) + "\r\n" +
                "\texpire    : " + Tools.toHex (Tools.intToBytes (expire)) + "\r\n" +
                "\tcard no   : " + Tools.toHex (Tools.intToBytes (cardNo)) + "\r\n" +
                "\tmain ver  : " + String.format ("0x%02X", mainVersion) + "\r\n" +
                "\tmin ver   : " + String.format ("02%02X", minVersion) + "\r\n" +
                "\ttail      : " + String.format ("0x%04X", tail) + "\r\n}";
    }

    public byte[] toByteArray () {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
//            baos.write (Tools.intToBytes (header, 2, true));
            baos.write (action);
            baos.write (admin);
            baos.write (Tools.intToBytes (id, 2, true));
            baos.write (Tools.intToBytes (timestamp, 4, true));
            baos.write (Tools.intToBytes (expire, 4, true));
            baos.write (Tools.intToBytes (cardNo, 4, true));
            baos.write (mainVersion);
            baos.write (minVersion);

            byte[] buff = baos.toByteArray ();
            int sum = 0;
            for (byte b : buff) sum += b & 0xff;
            sum &= 0xff;

            baos = new ByteArrayOutputStream ();
            baos.write (Tools.intToBytes (header, 2, true));
            baos.write (buff);
            baos.write (sum);
            baos.write (Tools.intToBytes (tail, 2, true));
            return baos.toByteArray ();
        } catch (IOException ex) {
            throw new RuntimeException (ex);
        }
    }
}