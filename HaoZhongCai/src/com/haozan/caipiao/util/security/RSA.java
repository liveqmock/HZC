package com.haozan.caipiao.util.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;

import javax.crypto.Cipher;

import android.content.Context;

public final class RSA {

    private static Context context;
    private static String privateKeyPlace, publicKeyPlace;

    public static final int RSA_ENCODE = UUID.randomUUID().hashCode(),
        RSA_DECODE = UUID.randomUUID().hashCode();

    private RSA() {
    }
    
    public final static byte[] rsaEncrypt(Context context, String src) {
        int mode = RSA.RSA_ENCODE;
        String file = "public.key";
        try {
            return RSA.getInstance(context, mode, file).encode(src);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RSA getInstance(Context contest, int mode, String file) {
        context = contest;
        if (mode == RSA_ENCODE)
            publicKeyPlace = file;
        else if (mode == RSA_DECODE)
            privateKeyPlace = file;
        else
            throw new RuntimeException(new IllegalArgumentException("type mode not supported"));
        return new RSA();
    }

    public byte[] encode(String src)
        throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyPlace));
// cipher.init(Cipher.ENCRYPT_MODE, getPublicKey("public.key"));
        return cipher.doFinal(src.getBytes("utf-8"));
    }

    public byte[] decode(byte[] code)
        throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyPlace));
        return cipher.doFinal(code);
    }

    public static void keyGenerator(int keySize, String publicKeyPlace, String privateKeyPlace)
        throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize);
        KeyPair kp = kpg.genKeyPair();
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = kf.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = kf.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
        storeKey(publicKeyPlace, pub.getModulus(), pub.getPublicExponent());
        storeKey(privateKeyPlace, priv.getModulus(), priv.getPrivateExponent());
    }

    private static void storeKey(String fileName, BigInteger ms, BigInteger pe)
        throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        try {
            oos.writeObject(ms);
            oos.writeObject(pe);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            oos.close();
        }
    }

    public static PublicKey getPublicKey(String keyFileName)
        throws IOException {
        InputStream input = context.getClass().getResourceAsStream("/assets/" + keyFileName);
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFileName));
        ObjectInputStream ois = new ObjectInputStream(input);
        try {
            BigInteger m = (BigInteger) ois.readObject();
            BigInteger e = (BigInteger) ois.readObject();
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(m, e));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            ois.close();
        }
    }

    public static PrivateKey getPrivateKey(String keyFileName)
        throws IOException {
        InputStream input = context.getClass().getResourceAsStream("/assets/" + keyFileName);
// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFileName));
        ObjectInputStream ois = new ObjectInputStream(input);
        try {
            BigInteger m = (BigInteger) ois.readObject();
            BigInteger e = (BigInteger) ois.readObject();
            return KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(m, e));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            ois.close();
        }
    }
}