package com.cng.desktop.card.concurrent;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by seth on 16-1-14
 */
public abstract class CancelableThread extends Thread implements ICancelable {
    protected boolean running = false;
    private static final Logger logger = Logger.getLogger (CancelableThread.class);

    private static final Set<CancelableThread> references = new HashSet<> ();

    protected abstract void doWork ();
    protected void beforeCancel () {}

    public static void dispose () {
        if (references != null && !references.isEmpty ()) {
            for (CancelableThread ct : references) {
                if (!ct.isCanceled ())
                    ct.cancel (true);
            }
        }
    }

    public CancelableThread () {
        references.add (this);
    }

    public CancelableThread (String name) {
        super (name);
        references.add (this);
    }

    public CancelableThread (String name, boolean running) {
        super (name);
        references.add (this);
        this.running = running;
        if (running) {
            start ();
        }
    }

    @Override
    public void cancel (boolean block) {
        beforeCancel ();
        if (logger.isDebugEnabled ()) {
            logger.debug ("trying to stop " + getName () + "...");
        }
        running = false;
        if (block && (Thread.currentThread () != this))
            try {
                this.join ();
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        synchronized (references) {
            if (references.contains (this))
                references.remove (this);
        }
        logger.info ("Server [" + getName () + "] stopped.");
    }

    @Override
    public boolean isCanceled () {
        return running;
    }

    @Override
    public void run () {
        if (logger.isDebugEnabled ())
            logger.debug ("Starting thread[" + getName () + "]");
        while (running) {
            try {
                doWork ();
            } catch (Exception ex) {
                logger.warn (ex.getMessage (), ex);
            }
        }
        if (logger.isDebugEnabled ())
            logger.debug ("Thread[" + getName () + "] stopped.");
    }
}