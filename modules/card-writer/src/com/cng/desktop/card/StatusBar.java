package com.cng.desktop.card;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Created by game on 2016/3/7
 */
public class StatusBar {
    private JLabel txtMessage;
    private JLabel txtComm;
    private JPanel root;

    public StatusBar () {
        Border border = BorderFactory.createCompoundBorder ();
        txtMessage.setBorder (border);
        txtComm.setBorder (border);
    }

    public JPanel getRootPanel () {
        return root;
    }

    public void setMessage (String message) {
        txtMessage.setText (message);
    }

    public void setCommName (String name) {
        txtComm.setText (name);
    }
}
