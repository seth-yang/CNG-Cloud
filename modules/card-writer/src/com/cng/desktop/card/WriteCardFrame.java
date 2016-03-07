package com.cng.desktop.card;

import javax.swing.*;

/**
 * Created by game on 2016/3/7
 */
public class WriteCardFrame extends JInternalFrame {
    private JCheckBox chkAdmin;
    private JTree tree1;
    private JPanel root;
    private JButton btnRead;
    private JButton btnWrite;

    public WriteCardFrame () {
        super("写入卡", true, true, true);
        setContentPane (root);
    }
}
