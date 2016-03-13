package com.cng.desktop.card.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by game on 2016/3/14
 */
public class NetworkUtil {
    public static String getLocalMacAddress () throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces ();
        while (en.hasMoreElements ()) {
            NetworkInterface ni = en.nextElement ();
            if (ni.isLoopback () || ni.isVirtual () || !ni.isUp ()) continue;
            byte[] address = ni.getHardwareAddress ();
            if (address != null) {
                StringBuilder builder = new StringBuilder ();
                for (byte b : address) {
                    if (builder.length () > 0) builder.append (':');
                    builder.append (String.format ("%02X", b));
                }
                return builder.toString ();
            }
        }

        return null;
    }
}