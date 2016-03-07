package com.cng.desktop.card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by game on 2016/3/7
 */
public class MainFrame extends JFrame implements ActionListener {
    private JDesktopPane desktop = new JDesktopPane ();
    private StatusBar status;

    public MainFrame () {
        super ("Card Manager - v1.0");
        JToolBar bar = new JToolBar ();
        bar.setFloatable (false);
        JPanel root = (JPanel) getContentPane ();
        root.setLayout (new BorderLayout ());
        root.add (bar, BorderLayout.NORTH);
        root.add (desktop, BorderLayout.CENTER);
        root.add ((status = new StatusBar ()).getRootPanel (), BorderLayout.SOUTH);
        status.setCommName ("COM6");

        JButton button = new JButton ("button 1");
        button.addActionListener (this);
        bar.add (button);

        pack ();
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        setVisible (true);
    }

    private void guiSetup () {


    }

    public static void main (String[] args) throws Exception {
        UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        new MainFrame () ;
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        switch (e.getActionCommand ()) {
            case "button 1" :
                JInternalFrame frame = new WriteCardFrame ();
                desktop.add (frame);
                frame.pack ();
                frame.setVisible (true);
                break;
        }
    }
}