package com.cng.cloud.web;

import com.cng.cloud.util.CloudKeyFetcherFactory;
import org.dreamwork.secure.AlgorithmMapping;
import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.secure.SecureContext;
import org.dreamwork.secure.SecureUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by game on 2016/2/23
 */
public class StartupListener implements ServletContextListener {
    @Override
    public void contextInitialized (ServletContextEvent event) {
        System.setProperty ("org.dreamwork.secure.provider", "org.bouncycastle.jce.provider.BouncyCastleProvider");
        SecureContext context = new SecureContext ();
        context.setBlockEncryption (AlgorithmMapping.BlockEncryption.AES128_CBC);
        context.setKeyTransport (AlgorithmMapping.KeyTransport.RSA_OAEP_MGF1P);

        ServletContext application = event.getServletContext ();
        CloudKeyFetcherFactory factory = new CloudKeyFetcherFactory (application);
        IKeyFetcher fetcher = factory.getKeyFetcher ();
        application.setAttribute ("org.dreamwork.key.fetcher", fetcher);
        application.setAttribute ("org.dreamwork.key.context", context);
    }

    @Override
    public void contextDestroyed (ServletContextEvent servletContextEvent) {

    }
}