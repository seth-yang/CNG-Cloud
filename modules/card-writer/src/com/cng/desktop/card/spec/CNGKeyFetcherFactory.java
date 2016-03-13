package com.cng.desktop.card.spec;

import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.secure.KeyFetcherFactory;

/**
 * Created by game on 2016/3/14
 */
public class CNGKeyFetcherFactory extends KeyFetcherFactory {
    private CNGKeyFetcher fetcher;

    @Override
    public IKeyFetcher getKeyFetcher () {
        if (fetcher == null) {
            fetcher = new CNGKeyFetcher ();
        }

        return fetcher;
    }
}
