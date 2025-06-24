package com.zkyzn.project_manager.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * key生成工具类
 * @author Zhang Fan
 */
public class KeyGenerateUtil {

    public static void generateKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());

        System.out.println("Public Key:\n" + publicKey);
        System.out.println("Private Key:\n" + privateKey);
    }

    public static void main(String[] args) throws Exception {
        generateKeys();
    }
}
