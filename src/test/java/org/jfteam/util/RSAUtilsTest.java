package org.jfteam.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-15
 * Time: 下午10:19
 */
public class RSAUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtilsTest.class);

    public static final String password = "123456";

    @Test
    public void encryptByPrivateKey() throws Exception {
        Map<String, String> keys = RSAUtils.getKeys();
        String publicKey = keys.get("publicKey");
        String privateKey = keys.get("privateKey");
        LOGGER.info("Public Key is : {}", publicKey);
        LOGGER.info("Private Key is : {}", privateKey);
        String encrypt = RSAUtils.encryptByPrivateKey(password, privateKey);
        LOGGER.info("Private Key encrypt : {}", encrypt);
        String decrypt = RSAUtils.decryptByPublicKey(encrypt, publicKey);
        LOGGER.info("Public Key decrypt : {}", decrypt);
    }

    @Test
    public void encryptByPublicKey() throws Exception {
        Map<String, String> keys = RSAUtils.getKeys();
        String publicKey = keys.get("publicKey");
        String privateKey = keys.get("privateKey");
        String encrypt = RSAUtils.encryptByPublicKey(password, publicKey);
        LOGGER.info("Private Key encrypt : {}", encrypt);
        String decrypt = RSAUtils.decryptByPrivateKey(encrypt, privateKey);
        LOGGER.info("Public Key decrypt : {}", decrypt);
    }

}