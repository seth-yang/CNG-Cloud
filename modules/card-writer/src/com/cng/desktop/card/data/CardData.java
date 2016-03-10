package com.cng.desktop.card.data;

import com.cng.desktop.card.util.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by game on 2016/3/9
 */
public class CardData {
    public boolean admin;
    public Date expire;
    public int cardNo, mainVersion, minVersion;

    @Override
    public String toString () {
        return "Card Data - {\r\n" +
                "\tadmin   : " + admin + "\r\n" +
                "\texpire  : " + (new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (expire)) + "\r\n" +
                "\tcard no : " + cardNo + "\r\n" +
                "\tversion : " + mainVersion + "." + minVersion + "\r\n}";
    }

    public static CardData parse (byte[] buff, int offset) {
        CardData data = new CardData ();
        int n = buff [offset + 2] & 0xff;
        data.admin = (n & 0x80) != 0;
        data.mainVersion = ((n >> 3) & 0x03);
        data.minVersion  = (n & 0x03);
        long ts = Tools.bytesToInt (buff, offset + 3, 4, false) * 1000L;
        data.expire = new Date (ts);
        data.cardNo = Tools.bytesToInt (buff, offset + 7, 4, false);
        return data;
    }
}