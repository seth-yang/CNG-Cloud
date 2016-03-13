package com.cng.cloud.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dreamwork.secure.RSAKeyFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by game on 2016/3/14
 */
public class SecureUtil {
    public static void main (String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance ("RSA", new BouncyCastleProvider ());
        generator.initialize (2048);
        KeyPair pair = generator.generateKeyPair ();
        PrivateKey privateKey = pair.getPrivate ();
        PublicKey  publicKey  = pair.getPublic ();

        File file = new File ("e:/tmp/cng-rsa-key.private");
        try (FileOutputStream fos = new FileOutputStream (file)) {
            fos.write (privateKey.getEncoded ());
            fos.flush ();
        }

        file = new File ("e:/tmp/cng-rsa-key.public");
        try (FileOutputStream fos = new FileOutputStream (file)) {
            fos.write (publicKey.getEncoded ());
            fos.flush ();
        }
    }
}