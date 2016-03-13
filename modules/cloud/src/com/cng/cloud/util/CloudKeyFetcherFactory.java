package com.cng.cloud.util;

import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.secure.KeyFetcherFactory;
import org.dreamwork.util.IOUtil;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by game on 2016/3/14
 */
public class CloudKeyFetcherFactory extends KeyFetcherFactory {
    private ServletContext context;

    public CloudKeyFetcherFactory (ServletContext context) {
        this.context = context;
    }

    private IKeyFetcher fetcher;
    @Override
    public IKeyFetcher getKeyFetcher () {
        if (fetcher == null) {
            fetcher = new CloudKeyFetcher (context);
        }

        return fetcher;
    }

    public static final class CloudKeyFetcher implements IKeyFetcher {
        private ServletContext context;

        private CloudKeyFetcher (ServletContext context) {
            this.context = context;
        }

        @Override
        public PrivateKey getPrivateKey (String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
            String resourcePath = "/WEB-INF/keys/cng-rsa-key.private";
            try (InputStream in = context.getResourceAsStream (resourcePath)) {
                byte[] data = IOUtil.read (in);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec (data);
                return KeyFactory.getInstance ("RSA").generatePrivate (spec);
            } catch (Exception ex) {
                ex.printStackTrace ();
                throw new RuntimeException (ex);
            }
        }

        @Override
        public PublicKey getPublicKey (String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
            String path = "/WEB-INF/keys/cng-rsa-key.public";
            try (InputStream in = context.getResourceAsStream (path)) {
                byte[] buff = IOUtil.read (in);
                X509EncodedKeySpec spec = new X509EncodedKeySpec (buff);
                KeyFactory factory = KeyFactory.getInstance ("rsa");
                return factory.generatePublic (spec);
            } catch (Exception ex) {
                throw new RuntimeException (ex);
            }
        }

        @Override
        public X509Certificate getCertificate (String issuer) throws CertificateException {
            return null;
        }
    }
}