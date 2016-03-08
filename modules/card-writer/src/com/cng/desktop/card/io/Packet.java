package com.cng.desktop.card.io;

import com.cng.desktop.card.util.Tools;
import org.dreamwork.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by game on 2016/3/8
 */
public class Packet {
    public static final int STATE_OK = 0, STATE_FAIL = 1;

    public int header, tail;
    public int state, type, length, crc;
    public byte[] data;

    public static final Packet DISPOSE = new Packet ();

    @Override
    public String toString () {
        return "Packet - {\r\n" +
                "\t header : " + String.format ("%04X", header) + "\r\n" +
                "\t state  : " + String.format ("%02X", state) + "\r\n" +
                "\t type   : " + String.format ("%02X", type) + "\r\n" +
                "\t length : " + String.format ("%02X", length) + "\r\n" +
                "\t data   : " + Tools.toHex (data) + "\r\n" +
                "\t tail   : " + String.format ("%04X", tail) + "\r\n}";
    }

    public byte[] toByteArray () {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            DataOutputStream dos = new DataOutputStream (baos);
            dos.writeShort (header);
            dos.write (state);
            dos.write (type);
            dos.write (length);
            dos.write (data);
            dos.write (crc);
            dos.writeShort (tail);
            dos.flush ();
            return baos.toByteArray ();
        } catch (IOException ex) {
            throw new RuntimeException (ex);
        }
    }
}