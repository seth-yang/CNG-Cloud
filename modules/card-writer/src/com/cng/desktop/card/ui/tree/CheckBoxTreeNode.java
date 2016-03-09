package com.cng.desktop.card.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by game on 2016/3/10
 */
public class CheckBoxTreeNode extends DefaultMutableTreeNode {
    protected boolean selected;

    public CheckBoxTreeNode () {
        this (null);
    }

    public CheckBoxTreeNode (Object userObject) {
        this (userObject, true, false);
    }

    public CheckBoxTreeNode (Object userData, boolean allowChildren, boolean selected) {
        super (userData, allowChildren);
        this.selected = selected;
    }

    public boolean isSelected () {
        return selected;
    }

    public void setSelected (boolean selected) {
        this.selected = selected;
        if (selected) {
            if (children != null) {
                for (Object o : children) {
                    CheckBoxTreeNode node = (CheckBoxTreeNode) o;
                    if (!node.isSelected ()) {
                        node.setSelected (selected);
                    }
                }
            }

            CheckBoxTreeNode node = (CheckBoxTreeNode) parent;
            if (node != null) {
                for (int i = 0; i < node.children.size (); i ++) {

                }
            }
        }
    }
}