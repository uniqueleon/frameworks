package org.aztec.framework.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.aztec.framework.core.common.exceptions.UnexpectedException;

public class CodecUtils {

    /**
     * @return an UUID String
     */
    public static String UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Encode a String to base64
     * 
     * @param value The plain String
     * @return The base64 encoded String
     */
    public static String encodeBASE64Str(String value) {
        try {
            return Base64.getEncoder().encodeToString(value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Encode binary data to base64
     * 
     * @param value The binary data
     * @return The base64 encoded String
     */
    public static String encodeBASE64Str(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    /**
     * Decode a base64 value
     * 
     * @param value The base64 encoded String
     * @return The binary data
     */
    public static byte[] decodeBASE64Binary(String value) {
        try {
            return Base64.getDecoder().decode(value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Decode a base64 value
     * 
     * @param value The base64 encoded String
     * @return The base64 decoded String
     */
    public static String decodeBASE64Str(String value) {
        try {
            byte[] bValue = Base64.getDecoder().decode(value.getBytes("utf-8"));
            return new String(bValue, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Decode a base64 value
     * 
     * @param value The binary data
     * @return The base64 decoded String
     */
    public static String decodeBASE64Str(byte[] value) {
        try {
            byte[] bValue = Base64.getDecoder().decode(value);
            return new String(bValue, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Build an hexadecimal MD5 hash for a String
     * 
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexMD5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Build an hexadecimal SHA1 hash for a String
     * 
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexSHA1(String value) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Write a byte array as hexadecimal String.
     */
    public static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * Transform an hexadecimal String to a byte array.
     */
    public static byte[] hexStringToByte(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw new UnexpectedException(e);
        }
    }

    /** 加密 */
    public static byte[] encryptAES(String value, String privateKey) {
        try {
            // 密钥填充
            privateKey = keyFill(privateKey);
            byte[] raw = privateKey.substring(0, 16).getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return cipher.doFinal(value.getBytes("utf-8"));
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /** 解密 */
    public static String decryptAES(byte[] value, String privateKey) {
        try {
            // 密钥填充
            privateKey = keyFill(privateKey);
            byte[] raw = privateKey.substring(0, 16).getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return new String(cipher.doFinal(value), "utf-8");
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * RSA加密
     * 
     * @param data
     * @param publickey
     * @return
     * @throws Exception
     */
    public static byte[] encryptRSA(byte[] data, String publickey) throws Exception {
        try {
            // 对公钥解密
            byte[] keyBytes = decodeBASE64Binary(publickey);

            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key publicKey = keyFactory.generatePublic(x509KeySpec);

            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cipherData = null;
            while (data.length - offset > 0) {
                if (data.length - offset > 117) {
                    cipherData = cipher.doFinal(data, offset, 117);
                } else {
                    cipherData = cipher.doFinal(data, offset, data.length - offset);
                }
                out.write(cipherData);
                offset += 117;
            }
            cipherData = out.toByteArray();
            out.close();
            return cipherData;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * RSA解密
     * 
     * @param data
     * @param privatekey
     * @return
     * @throws Exception
     */
    public static byte[] decryptRSA(byte[] data, String privatekey) throws Exception {
        try {
            // 对密钥解密
            byte[] keyBytes = decodeBASE64Binary(privatekey);

            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] cipherData = null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            while (data.length - offset > 0) {
                if (data.length - offset > 128) {
                    cipherData = cipher.doFinal(data, offset, 128);
                } else {
                    cipherData = cipher.doFinal(data, offset, data.length - offset);
                }
                out.write(cipherData);
                offset += 128;
            }
            cipherData = out.toByteArray();
            out.close();
            return cipherData;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /** 密钥填充，补齐16位 */
    private static String keyFill(String privateKey) {
        if (privateKey.length() < 16) {
            int len = 16 - privateKey.length();
            for (int i = 0; i < len; i++) {
                privateKey += "0";
            }
        }
        return privateKey;
    }

    /**
     * 使用HMAC-SHA1 签名方法对对encryptText进行签名
     * 
     * @param encryptText
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static byte[] hmacSHA1Encrypt(String encryptKey, String encryptText) throws Exception {
        byte[] data = encryptKey.getBytes("utf-8");
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定Mac算法的Mac对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化Mac对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes("utf-8");
        // 完成Mac操作
        return mac.doFinal(text);
    }

}
