package org.jfteam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-15
 * Time: 下午9:33
 */
public class RSAUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtils.class);

    private static final String RSA = "RSA";

    private static final int RSA_LENGTH = 1024;

    private static KeyPairGenerator kpg = null;

    private static Cipher cipher = null;

    static {
        init();
    }

    public static void init() {
        try {
            if (kpg == null) {
                kpg = KeyPairGenerator.getInstance(RSA);
            }
            if (cipher == null) {
                cipher = Cipher.getInstance(RSA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成RSA密钥对(默认密钥长度为1024)
     *
     * @return
     */
    private static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(RSA_LENGTH);
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048<br> 一般1024
     * @return
     */
    private static KeyPair generateRSAKeyPair(int keyLength) {
        kpg.initialize(keyLength);
        return kpg.genKeyPair();
    }

    public static Map<String, String> getKeys() {
        Map<String, String> keys = new HashMap<>();
        final KeyPair keyPair = generateRSAKeyPair();
        keys.put("publicKey", getPublicKey(keyPair));
        keys.put("privateKey", getPrivateKey(keyPair));
        return keys;
    }

    private static String getPublicKey(KeyPair keyPair) {
        final PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    private static String getPrivateKey(KeyPair keyPair) {
        final PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    private static PublicKey createPublicKey(byte[] keyBytes) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            LOGGER.error("RSAUtils getPublicKey failure with {}.", e);
        }
        return null;
    }

    private static PrivateKey createPrivateKey(byte[] keyBytes) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            LOGGER.error("RSAUtils getPrivateKey failure with {}.", e);
        }
        return null;
    }

    private static String encrypt(String src, Key key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(src.getBytes(SysConstants.DEFAULT_CHARSET)));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LOGGER.error("RSAUtils encrypt failure with {}.", e, key.getClass().getSimpleName());
        }
        return null;
    }

    private static String decrypt(String src, Key key) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(src.getBytes(SysConstants.DEFAULT_CHARSET)));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LOGGER.error("RSAUtils decrypt failure with {}.", e, key.getClass().getSimpleName());
        }
        return null;
    }

    public static String encryptByPrivateKey(String src, String privateKey) {
        final PrivateKey key = createPrivateKey(Base64.getDecoder().decode(privateKey.getBytes(SysConstants.DEFAULT_CHARSET)));
        return encrypt(src, key);
    }

    public static String encryptByPublicKey(String src, String publicKey) {
        final PublicKey key = createPublicKey(Base64.getDecoder().decode(publicKey.getBytes(SysConstants.DEFAULT_CHARSET)));
        return encrypt(src, key);
    }

    public static String decryptByPrivateKey(String src, String privateKey) {
        final PrivateKey key = createPrivateKey(Base64.getDecoder().decode(privateKey.getBytes(SysConstants.DEFAULT_CHARSET)));
        return decrypt(src, key);
    }

    public static String decryptByPublicKey(String src, String publicKey) {
        final PublicKey key = createPublicKey(Base64.getDecoder().decode(publicKey.getBytes(SysConstants.DEFAULT_CHARSET)));
        return decrypt(src, key);
    }

}
