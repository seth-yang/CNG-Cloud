package com.cng.desktop.card.ui;

import javax.swing.*;
import java.awt.event.*;

public class SetupDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox cmbPort;

    public SetupDialog () {
        setContentPane (contentPane);
        setModal (true);
        getRootPane ().setDefaultButton (buttonOK);

        buttonOK.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onOK ();
            }
        });

        buttonCancel.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onCancel ();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
        addWindowListener (new WindowAdapter () {
            public void windowClosing (WindowEvent e) {
                onCancel ();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                onCancel ();
            }
        }, KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK () {
// add your code here
        dispose ();
    }

    private void onCancel () {
// add your code here if necessary
        dispose ();
    }

    public static void main (String[] args) {
        SetupDialog dialog = new SetupDialog ();
        dialog.pack ();
        dialog.setVisible (true);
        System.exit (0);
    }
}
