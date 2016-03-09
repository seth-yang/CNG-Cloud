package com.cng.desktop.card.concurrent;

/**
 * Created by game on 2016/3/10
 */
public class MessageHandlerDelegate extends MessageHandler {
    private IMessageHandler handler;

    public MessageHandlerDelegate (IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleUIMessage (Message message) {
        handler.handleUIMessage (message);
    }

    @Override
    public void handleNonUIMessage (Message message) {
        handler.handleNonUIMessage (message);
    }
}