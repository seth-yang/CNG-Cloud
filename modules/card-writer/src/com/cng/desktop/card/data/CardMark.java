package com.cng.desktop.card.data;

import com.cng.desktop.card.util.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by game on 2016/3/9
 */
public class CardMark {
    public int index, mainVersion, minVersion;
    public Date timestamp;

    @Override
    public String toString () {
        return "CardMark - {\r\n" +
                "\tindex     : " + index + "\r\n" +
                "\ttimestamp : " + (new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (timestamp)) + "\r\n" +
                "\tversion   : " + mainVersion + "." + minVersion + "\r\n}";
    }

    public static CardMark parse (byte[] buff, int offset) {
        CardMark mark = new CardMark ();
        mark.index = Tools.bytesToInt (buff, offset + 2, 2, false);
        long ts = Tools.bytesToInt (buff, offset + 4, 4, false) * 1000L;
        mark.timestamp = new Date (ts);
        mark.mainVersion = buff [offset + 8] & 0xff;
        mark.minVersion  = buff [offset + 9] & 0xff;
        return mark;
    }
}