package com.cng.desktop.card.io;

/**
 * Created by game on 2016/3/10
 */
public class ActionCommand extends Command {
    public ActionCommand (int action) {
        this.action = action;
    }

    @Override
    public byte[] toByteArray () {
        byte[] buff = new byte[23];
        buff[ 0] = (byte) 0xfe;
        buff[ 1] = (byte) 0xca;
        buff[ 2] = (byte) action;  // action
        buff[20] = (byte) action;  // CRC
        buff[21] = (byte) 0xbe;
        buff[22] = (byte) 0xba;

        return buff;
    }
}