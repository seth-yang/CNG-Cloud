package com.cng.desktop.card.concurrent;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by game on 2016/3/10
 */
public abstract class MessageHandler implements IMessageHandler {
    private static final ExecutorService service = Executors.newCachedThreadPool ();
    private static final int TYPE_UI = 0, TYPE_NON_UI = 1;

    public void sendUIMessage (Message message) {
        SwingUtilities.invokeLater (new MessageRunner (this, message, TYPE_UI));
    }

    public void sendUIMessage (int what) {
        sendUIMessage (new Message (what));
    }

    public void sendUIMessage (int what, Object value) {
        sendUIMessage (new Message (what, value));
    }

    public void sendNonUIMessage (Message message) {
        service.execute (new MessageRunner (this, message, TYPE_NON_UI));
    }

    public void sendNonUIMessage (int what) {
        sendNonUIMessage (new Message (what));
    }

    public void sendNonUIMessage (int what, Object value) {
        sendNonUIMessage (new Message (what, value));
    }

    private static class MessageRunner implements Runnable {
        IMessageHandler handler;
        Message message;
        int type;

        MessageRunner (IMessageHandler handler, Message message, int type) {
            this.handler = handler;
            this.message = message;
            this.type    = type;
        }

        @Override
        public void run () {
            if (type == TYPE_UI) {
                handler.handleUIMessage (message);
            } else {
                handler.handleNonUIMessage (message);
            }
        }
    }
}