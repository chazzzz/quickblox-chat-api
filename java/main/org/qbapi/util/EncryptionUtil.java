package org.qbapi.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chazz on 6/10/2015.
 */
public class EncryptionUtil {

	public static String encryptHmac(String text, String secret) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);

        byte[] result = mac.doFinal(text.getBytes());

        byte[] hexBytes = new Hex().encode(result);

        return new String(hexBytes, "UTF-8");
    }

}
