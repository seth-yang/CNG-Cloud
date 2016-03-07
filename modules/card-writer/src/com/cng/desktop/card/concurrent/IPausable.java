package com.cng.desktop.card.concurrent;

/**
 * Created by seth on 16-1-11
 */
public interface IPausable {
    void pause ();
    void proceed ();
    boolean isPaused ();
}