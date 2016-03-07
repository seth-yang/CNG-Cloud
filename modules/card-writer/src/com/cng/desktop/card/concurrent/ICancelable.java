package com.cng.desktop.card.concurrent;

/**
 * Created by seth on 16-1-11
 */
public interface ICancelable {
    boolean isCanceled ();
    void cancel (boolean block) throws InterruptedException;
}