package com.cng.desktop.card.spec;

import org.dreamwork.secure.IKeyFetcher;
import org.dreamwork.util.IOUtil;

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
public class CNGKeyFetcher implements IKeyFetcher {
    @Override
    public PrivateKey getPrivateKey (String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String resource = "META-INF/cng-rsa-key.private";
        try (InputStream in = getClass ().getClassLoader ().getResourceAsStream (resource)) {
            byte[] buff = IOUtil.read (in);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec (buff);
            return KeyFactory.getInstance ("RSA").generatePrivate (spec);
        } catch (Exception ex) {
            ex.printStackTrace ();
            throw new RuntimeException (ex);
        }
    }

    @Override
    public PublicKey getPublicKey (String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String path = "META-INF/cng-rsa-key.public";
        try (InputStream in = getClass ().getClassLoader ().getResourceAsStream (path)) {
            byte[] buff = IOUtil.read (in);
            X509EncodedKeySpec spec = new X509EncodedKeySpec (buff);
            KeyFactory factory = KeyFactory.getInstance ("rsa");
            PublicKey key = factory.generatePublic (spec);
            // todo: validate the public key.
            return key;
        } catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }

    @Override
    public X509Certificate getCertificate (String issuer) throws CertificateException {
        return null;
    }
}
