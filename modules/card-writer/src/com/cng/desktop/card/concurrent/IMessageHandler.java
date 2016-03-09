package com.cng.desktop.card.concurrent;

/**
 * Created by game on 2016/3/10
 */
public interface IMessageHandler {
    void handleUIMessage (Message message);
    void handleNonUIMessage (Message message);
}
