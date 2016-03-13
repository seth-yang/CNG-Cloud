package com.cng.desktop.card;

import com.cng.desktop.card.concurrent.IMessageHandler;
import com.cng.desktop.card.concurrent.Message;
import com.cng.desktop.card.concurrent.MessageHandler;
import com.cng.desktop.card.concurrent.MessageHandlerDelegate;
import com.cng.desktop.card.data.CardData;
import com.cng.desktop.card.data.CardMark;
import com.cng.desktop.card.io.Command;
import com.cng.desktop.card.io.ISerialConnectListener;
import com.cng.desktop.card.io.Packet;
import com.cng.desktop.card.io.Serial;
import com.cng.desktop.card.spec.CNGKeyFetcherFactory;
import com.cng.desktop.card.util.HttpUtil;
import com.cng.desktop.card.util.Tools;
import org.apache.log4j.Logger;
import org.dreamwork.secure.AlgorithmMapping;
import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.secure.SecureContext;
import org.dreamwork.secure.SecureUtil;
import org.dreamwork.util.IOUtil;
import org.dreamwork.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * Created by game on 2016/3/7
 */
public class WriteCardFrame extends JFrame implements IMessageHandler, ActionListener, ISerialConnectListener {
    private JCheckBox chkAdmin;
    private JPanel root;
    private JButton btnRead, btnWrite, btnClear, btnExit;
    private JLabel txtCardNo, txtWriteDate, txtAddPlace;
    private JTextField txtUserName;
    private JComboBox<String> cmbPorts;
    private JComboBox<Integer> cmbYear, cmbMonth, cmbDay;
    private JLabel txtNewCard;
    private Serial serial;
    private SerialMonitor monitor;

    private MessageHandler handler = new MessageHandlerDelegate (this);

    private SecureContext context = new SecureContext ();

    private static final int FIND_PORTS         =  0;
    private static final int SHOW_ERROR         =  1;
    private static final int SHOW_MESSAGE       =  2;
    private static final int READ_CARD          =  3;
    private static final int WRITE_CARD         =  4;
    private static final int ERASE_CARD         =  5;
    private static final int SET_BUTTON_STATUS  =  6;
    private static final int CHANGE_SERIAL_PORT =  7;
    private static final int UPDATE_UI          =  8;
    private static final int EDIT_PLACE         =  9;
    private static final int CREATE_NEW_CARD    = 10;


    private static final String CACHE_FILE_NAME = "cache.cng";
    private static final Logger logger          = Logger.getLogger (WriteCardFrame.class);
    private static final DecimalFormat df       = new DecimalFormat ("000000");
    private static final SimpleDateFormat sf    = new SimpleDateFormat ("yyyy-MM-dd");
    private static final String CLOUD_SERVER    = "http://localhost:8080/cng";

    public WriteCardFrame () {
        super ("写入卡");
        setContentPane (root);

        setSize (600, 600);

        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);

        guiSetup ();
        setVisible (true);

        handler.sendNonUIMessage (FIND_PORTS);
    }

    @Override
    public void handleUIMessage (Message message) {
        switch (message.what) {
            case SHOW_ERROR :
                JOptionPane.showMessageDialog (this, message.get (), "Error", JOptionPane.WARNING_MESSAGE);
                break;
            case SHOW_MESSAGE :
                JOptionPane.showMessageDialog (this, message.get (), "Success", JOptionPane.INFORMATION_MESSAGE);
                break;
            case UPDATE_UI :
                CardMark mark = (CardMark) message.data.get ("mark");
                CardData data = (CardData) message.data.get ("data");
                txtWriteDate.setText (sf.format (mark.timestamp));
                txtCardNo.setText (df.format (data.cardNo));
                chkAdmin.setSelected (data.admin);
                Calendar c = Calendar.getInstance ();
                c.setTime (data.expire);
                cmbYear.setSelectedItem (c.get (Calendar.YEAR));
                cmbMonth.setSelectedItem (c.get (Calendar.MONTH) + 1);
                cmbDay.setSelectedItem (c.get (Calendar.DAY_OF_MONTH));
                break;
            case SET_BUTTON_STATUS:
                Boolean value = (Boolean) message.get ();
                boolean enabled = (value != null && value);
                btnExit.setEnabled (enabled);
                btnRead.setEnabled (enabled);
                btnWrite.setEnabled (enabled);
                btnClear.setEnabled (enabled);
                cmbPorts.setEnabled (enabled);
                break;
            case CREATE_NEW_CARD :
                Integer no = (Integer) message.get ();
                if (no != null)
                    txtCardNo.setText (df.format (no));
                txtWriteDate.setText (sf.format (System.currentTimeMillis ()));
                break;
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
                break;
            case WRITE_CARD :
                writeCard ();
                break;
            case ERASE_CARD :
                erase ();
                break;
            case EDIT_PLACE :
                editPlace ();
                break;
            case CREATE_NEW_CARD :
                createNewCard ();
                break;
            case CHANGE_SERIAL_PORT :
                changeSerialPort ();
                break;
        }
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        Object sender = e.getSource ();
        if (sender == btnClear) {
            if (prompt ("您真的要消除这张卡吗？"))
                handler.sendNonUIMessage (ERASE_CARD);
        } else if (sender == btnRead) {
            handler.sendNonUIMessage (READ_CARD);
        } else if (sender == btnWrite) {
            handler.sendNonUIMessage (WRITE_CARD);
        } else if (sender == cmbPorts) {
            handler.sendNonUIMessage (CHANGE_SERIAL_PORT);
        } else if (sender == btnExit) {
            System.exit (0);
        }
    }

    MouseListener adapter = new MouseAdapter () {
        @Override
        public void mouseClicked (MouseEvent e) {
            Object sender = e.getSource ();
            if (sender == txtAddPlace) {
                handler.sendNonUIMessage (EDIT_PLACE);
            } else if (sender == txtNewCard) {
                createNewCard ();
//                handler.sendNonUIMessage (CREATE_NEW_CARD);
            }
        }

        @Override
        public void mouseEntered (MouseEvent e) {
            setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited (MouseEvent e) {
            setCursor (Cursor.getDefaultCursor ());
        }
    };

    private void guiSetup () {
        txtAddPlace.addMouseListener (adapter);
        txtNewCard.addMouseListener (adapter);

        btnClear.addActionListener (this);
        btnRead.addActionListener (this);
        btnWrite.addActionListener (this);
        btnExit.addActionListener (this);
    }

    private void findPorts () {
        java.util.List<String> ports = Serial.getAllSerialPorts ();
        cmbPorts.addItem ("");
        for (String name : ports) {
            cmbPorts.addItem (name);
        }

        for (int i = 2016; i < 2026; i ++) {
            cmbYear.addItem (i);
        }

        for (int i = 1; i <= 12; i ++)
            cmbMonth.addItem (i);

        for (int i = 1; i <= 31; i ++)
            cmbDay.addItem (i);

        File cache = new File (System.getProperty ("java.io.tmpdir"), CACHE_FILE_NAME);
        if (cache.exists ()) {
            InputStream in = null;
            try {
                in = new FileInputStream (cache);
                BufferedReader reader = new BufferedReader (new InputStreamReader (in, "utf-8"));
                String line = reader.readLine ();
                if (line != null) {
                    if (logger.isDebugEnabled ())
                        logger.debug ("saved serial port: " + line);
                    cmbPorts.setSelectedItem (line.trim ());
                    bindSerial (line.trim ());
                }
            } catch (IOException ex) {
                ex.printStackTrace ();
            } catch (Exception ex) {
                ex.printStackTrace ();
                handler.sendUIMessage (SHOW_ERROR, "连接串口：" + cmbPorts.getSelectedItem () + "失败");
            } finally {
                if (in != null) try {
                    in.close ();
                } catch (IOException e) {
                    //
                }
            }
        }
        cmbPorts.addActionListener (this);

        context.setBlockEncryption (AlgorithmMapping.BlockEncryption.AES128_CBC);
        context.setKeyTransport (AlgorithmMapping.KeyTransport.RSA_OAEP_MGF1P);

        try {
            initToken ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    private String token;
    private void initToken () throws IOException {
        String path = "META-INF/token";
        try (InputStream in = getClass ().getClassLoader ().getResourceAsStream (path)) {
            BufferedReader reader = new BufferedReader (new InputStreamReader (in, "utf-8"));
            String line = reader.readLine ();
            if (line != null) {
                token = line.trim ();
            }
            line = reader.readLine ();
            // todo: validate the token.
        }
    }

    private void bindSerial (String name) throws Exception {
        if (monitor != null) {
            SerialMonitor temp = monitor;
            monitor = null;
            temp.abort ();
        }

        if (serial != null) {
            Serial temp = serial;
            serial = null;
            temp.disconnect ();
        }

        if (StringUtil.isEmpty (name)) return;

        serial = new Serial (name);
        serial.setBaudRate (115200);
        serial.setConnectListener (this);
        serial.connect ();
        if (logger.isDebugEnabled ())
            logger.debug ("serial: " + name + " connected.");

        monitor = new SerialMonitor ();
        monitor.start ();
    }

    private void readCard () {
        try {
            serial.write (Command.READ);
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
    }

    private void writeCard () {
        if (!checkData ()) {
            return;
        }

        // fetch card no from server
        int cardNo = -1;
/*
        try {
            cardNo = fetchCardNo ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
*/
        try {
            String url = CLOUD_SERVER + "/card?" + token;
            HttpURLConnection conn = (HttpURLConnection) new URL (url).openConnection ();
            byte[] data = null;
            try {
                conn.setDoInput (true);
                conn.setRequestMethod ("POST");

                InputStream in = conn.getInputStream ();
                data = IOUtil.read (in);
            } finally {
                conn.disconnect ();
            }
/*
            HttpUtil.post (url);
*/
            if (data != null) {
                CNGKeyFetcherFactory factory = new CNGKeyFetcherFactory ();
                IKeyFetcher fetcher = factory.getKeyFetcher ();
                PublicKey key = fetcher.getPublicKey (null);
                SecureUtil util = new SecureUtil (context);
                data = util.decrypt (data, key);
                cardNo = Tools.bytesToInt (data);
            }
        } catch (Exception ex) {
            ex.printStackTrace ();
        }

        Command command = new Command ((short) 1, Command.ACTION_WRITE);
        command.admin = chkAdmin.isSelected () ? 1 : 0;
        command.cardNo = cardNo;

        Integer year = (Integer) cmbYear.getSelectedItem ();
        Integer month = (Integer) cmbMonth.getSelectedItem ();
        Integer day = (Integer) cmbDay.getSelectedItem ();
        Calendar c = Calendar.getInstance ();
        c.set (Calendar.YEAR, year);
        c.set (Calendar.MONTH, month - 1);
        c.set (Calendar.DAY_OF_MONTH, day);
        command.expire = (int) (c.getTimeInMillis () / 1000);
        command.majorVersion = 1;
        command.minorVersion = 0;
        command.timestamp = (int) (System.currentTimeMillis () / 1000);
        try {
            serial.write (command);
        } catch (IOException ex) {
            //
        }
        txtCardNo.setText (String.valueOf (cardNo));
    }

    private boolean checkData () {
        String userName = txtUserName.getText ().trim ();
        int year = (int) cmbYear.getSelectedItem ();
        int month = (int) cmbMonth.getSelectedItem ();
        int day = (int) cmbDay.getSelectedItem ();
        Calendar c = Calendar.getInstance ();
        c.set (Calendar.YEAR, year);
        c.set (Calendar.MONTH, month - 1);
        c.set (Calendar.DAY_OF_MONTH, day);
        if (c.getTimeInMillis () < System.currentTimeMillis ()) {
            handler.sendUIMessage (SHOW_ERROR, "有效期不能比今天更早！");
            return false;
        }

        if (userName.length () == 0) {
            handler.sendUIMessage (SHOW_ERROR, "请填写用户名");
            return false;
        }

        return true;
    }

    private void erase () {
        try {
            serial.write (Command.ERASE);
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
    }

    private void editPlace () {

    }

    private void createNewCard () {
        txtCardNo.setText ("");
        txtUserName.setText ("");
        Calendar now = Calendar.getInstance ();
        txtWriteDate.setText (sf.format (now.getTime ()));
        now.add (Calendar.YEAR, 1);
        now.add (Calendar.DAY_OF_MONTH, -1);
        cmbYear.setSelectedItem (now.get (Calendar.YEAR));
        cmbMonth.setSelectedIndex (now.get (Calendar.MONTH));
        cmbDay.setSelectedIndex (now.get (Calendar.DAY_OF_MONTH) - 1);
        chkAdmin.setSelected (false);
    }

    private boolean prompt (String message) {
        int ret = JOptionPane.showConfirmDialog (this, message, "问题", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return ret == JOptionPane.YES_OPTION;
    }

    private void changeSerialPort () {
        String name = (String) cmbPorts.getSelectedItem ();
        if (StringUtil.isEmpty (name))
            return;

        name = name.trim ();
        File cache = new File (System.getProperty ("java.io.tmpdir"), CACHE_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream (cache);
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (fos, "utf-8"));
            writer.write (name);
            writer.flush ();
            fos.flush ();
        } catch (IOException ex) {
            ex.printStackTrace ();
        } finally {
            if (fos != null) try {
                fos.close ();
            } catch (IOException ex) {
                //
            }
        }

        try {
            bindSerial (name);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static void main (String[] args) throws Exception {
        System.setProperty ("org.dreamwork.secure.provider", "org.bouncycastle.jce.provider.BouncyCastleProvider");
        UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        Object key, value;
        Font font = new Font ("MSYH", Font.PLAIN, 14);
        while (keys.hasMoreElements ()) {
            key = keys.nextElement ();
            value = UIManager.get (key);

            if (value instanceof Font) {
                UIManager.put (key, font);
            }
        }
        new WriteCardFrame ();
    }

    @Override
    public void onConnected () {
        handler.sendUIMessage (SET_BUTTON_STATUS, true);
    }

    @Override
    public void onDisconnected () {
        handler.sendUIMessage (SET_BUTTON_STATUS, false);
    }

    private class SerialMonitor extends Thread {
        private boolean running = true;
        private boolean paused  = true;

        private final Object locker = new byte[0];

        public void abort () {
            running = false;
            if (paused)
                paused = false;
            synchronized (locker) {
                locker.notifyAll ();
            }
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
            }
        }

        private void doWork () throws InterruptedException {
            Packet packet = serial.read ();
            System.err.println ("packet = " + packet);
            if (packet == Packet.DISPOSE) {
                abort ();
            } else if (packet != null) {
                if (packet.state == 0) {
                    handler.sendUIMessage (SHOW_ERROR, "操作失败");
                } else {
                    switch (packet.type) {
                        case Command.ACTION_READ :
                            if (packet.data [0] == 0) {
                                handler.sendUIMessage (SHOW_MESSAGE, "这是一张空白卡");
                            } else {
                                CardMark mark = CardMark.parse (packet.data, 0);
                                CardData data = CardData.parse (packet.data, 16);
                                Message message = new Message (UPDATE_UI);
                                message.data.put ("mark", mark);
                                message.data.put ("data", data);
                                handler.sendUIMessage (message);
                            }
                            break;
                        case Command.ACTION_WRITE :
                            handler.sendUIMessage (SHOW_MESSAGE, "写卡成功");
                            break;
                        case Command.ACTION_ERASE :
                            handler.sendUIMessage (SHOW_MESSAGE, "擦除成功");
                            break;
                    }
                }
            }
        }
    }
}