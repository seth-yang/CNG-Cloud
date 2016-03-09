package com.cng.desktop.card;

import com.cng.desktop.card.concurrent.IMessageHandler;
import com.cng.desktop.card.concurrent.Message;
import com.cng.desktop.card.concurrent.MessageHandler;
import com.cng.desktop.card.concurrent.MessageHandlerDelegate;
import com.cng.desktop.card.data.CardData;
import com.cng.desktop.card.data.CardMark;
import com.cng.desktop.card.io.Command;
import com.cng.desktop.card.io.Packet;
import com.cng.desktop.card.io.Serial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

/**
 * Created by game on 2016/3/7
 */
public class WriteCardFrame extends JFrame implements IMessageHandler, ActionListener {
    private JCheckBox chkAdmin;
    private JPanel root;
    private JButton btnRead;
    private JButton btnWrite;
    private JLabel txtCardNo;
    private JTextField txtUserName;
    private JLabel txtWriteDate;
    private JButton btnClear;
    private JComboBox<String> cmbPorts;
    private JLabel txtAddPlace;
    private JLabel txtExpire;
    private Serial serial;

    private MessageHandler handler = new MessageHandlerDelegate (this);

    private static final int FIND_PORTS = 0;
    private static final int SHOW_ERROR = 1;
    private static final int READ_CARD  = 2;

    public WriteCardFrame () {
        super("写入卡");
        setContentPane (root);

        setSize (600, 600);
        setVisible (true);

        setDefaultCloseOperation (DISPOSE_ON_CLOSE);

        guiSetup ();
        handler.sendNonUIMessage (FIND_PORTS);
    }

    @Override
    public void handleUIMessage (Message message) {
        switch (message.what) {
            case SHOW_ERROR :
                JOptionPane.showMessageDialog (this, message.data.get (Message.DEFAULT_KEY), "Error", JOptionPane.WARNING_MESSAGE);
                break;
            case UPDATE_UI :
                SimpleDateFormat sf = new SimpleDateFormat ("yyyy-MM-dd");
                DecimalFormat    df = new DecimalFormat ("000000");
                CardMark mark = (CardMark) message.data.get ("mark");
                CardData data = (CardData) message.data.get ("data");
                txtWriteDate.setText (sf.format (mark.timestamp));
                txtCardNo.setText (df.format (data.cardNo));
                chkAdmin.setSelected (data.admin);
                txtExpire.setText (sf.format (data.expire));
        }
    }

    @Override
    public void handleNonUIMessage (Message message) {
        switch (message.what) {
            case FIND_PORTS :
                findPorts ();
                break;
            case READ_CARD :
                readCard ();
        }
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        Object sender = e.getSource ();
        if (sender == btnClear) {

        } else if (sender == btnRead) {
            handler.sendNonUIMessage (READ_CARD);
        } else if (sender == btnWrite) {

        } else if (sender == cmbPorts) {

        }
    }

    private void guiSetup () {
        txtAddPlace.addMouseListener (new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {

            }

            @Override
            public void mouseEntered (MouseEvent e) {
                setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited (MouseEvent e) {
                setCursor (Cursor.getDefaultCursor ());
            }
        });

        btnClear.addActionListener (this);
        btnRead.addActionListener (this);
        btnWrite.addActionListener (this);

        cmbPorts.addActionListener (this);
    }

    private void findPorts () {
        java.util.List<String> ports = Serial.getAllSerialPorts ();
        for (String name : ports) {
            cmbPorts.addItem (name);
        }
    }

    private void readCard () {
        String portName = (String) cmbPorts.getSelectedItem ();
        System.out.println ("portName = " + portName);
        if (serial == null) {
            serial = new Serial (portName);
            serial.setBaudRate (115200);
            try {
                serial.connect ();
                new SerialMonitor ().start ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
        try {
            Command command = new Command ((short) 1, Command.ACTION_READ, true, System.currentTimeMillis (), System.currentTimeMillis (), 1);
            System.out.println (command);

/*
            for (int i = 3; i > 0; i --) {
                System.out.println (i + " ... ");
*/
                Thread.sleep (2000);
//            }
            serial.write (command);

        } catch (Exception ex) {
            ex.printStackTrace ();
        }
    }

    public static void main (String[] args) throws Exception {
        UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        Object key, value;
        Font font = new Font ("MSYH", Font.PLAIN, 16);
        while (keys.hasMoreElements ()) {
            key = keys.nextElement ();
            value = UIManager.get (key);

            if (value instanceof Font) {
                UIManager.put (key, font);
            }
        }
        new WriteCardFrame ();
    }

    private class SerialMonitor extends Thread {
        private boolean running = true;
        private boolean paused  = true;

        private final Object locker = new byte[0];

        public void abort () {
            running = false;
            if (paused)
                paused = false;

        }

        public void pause () {
            synchronized (locker) {
                paused = true;
            }
        }

        public void proceed () {
            synchronized (locker) {
                paused = false;
                locker.notifyAll ();
            }
        }

        @Override
        public void run () {
            while (running) {
                synchronized (locker) {
                    while (serial == null) {
                        try {
                            locker.wait ();
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                    }
                }

                try {
                    doWork ();
                } catch (Exception ex) {
                    ex.printStackTrace ();
                }
/*
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
*/
            }
        }

        private void doWork () throws InterruptedException {
            Packet packet = serial.read ();
            if (packet != null) {
                if (packet.state == 0) {
                    String error = "操作失败";
                    handler.sendUIMessage (SHOW_ERROR, error);
                } else if (packet.data [0] == 0) {
                    handler.sendUIMessage (SHOW_ERROR, "这是一张空白卡");
                } else {
                    CardMark mark = CardMark.parse (packet.data,  0);
                    CardData data = CardData.parse (packet.data, 16);
                    Message message = new Message (UPDATE_UI);
                    message.data.put ("mark", mark);
                    message.data.put ("data", data);
                    handler.sendUIMessage (message);
                }
            }
        }
    }

    private static final int UPDATE_UI = 100;
}