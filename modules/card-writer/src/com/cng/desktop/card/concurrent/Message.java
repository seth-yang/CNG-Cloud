package com.cng.desktop.card.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by game on 2016/3/10
 */
public class Message {
    public int what;
    public Map<String, Object> data = new HashMap<> ();

    public static final String DEFAULT_KEY = "com.cng.desktop.card.concurrent.Message.DEFAULT_KEY";

    public Message () {}

    public Message (int what) {
        this.what = what;
    }

    public Message (int what, Object value) {
        this (what);
        data.put (DEFAULT_KEY, value);
    }

    public Object get () {
        return data.get (DEFAULT_KEY);
    }
}