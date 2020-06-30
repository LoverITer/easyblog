package top.easyblog.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * AES加解密工具
 * AES-128: key和iv都是16个字节，16*8=128bit，java似乎只支持AES-128
 * @author HuangXin
 * @since 2020/1/19 15:26
 */
public class AESCryptUtils {
    /**
     * AES CBC 加密
     * @param message 需要加密的字符串
     * @param key   密匙
     * @param iv    IV，需要和key长度相同
     * @return  返回加密后密文，编码为base64
     */
    public static String encryptCBC(String message, String key, String iv) {
        final String cipherMode = "AES/CBC/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            byte[] content = new byte[0];
            content = message.getBytes(charsetName);
            //
            byte[] keyByte = key.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            //
            byte[] ivByte = iv.getBytes(charsetName);
            IvParameterSpec ivSpec = new IvParameterSpec(ivByte);

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] data = cipher.doFinal(content);
            final Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(data);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * AES CBC 解密
     * @param messageBase64 密文，base64编码
     * @param key   密匙，和加密时相同
     * @param iv    IV，需要和key长度相同
     * @return  解密后数据
     */
    public static String decryptCBC(String messageBase64, String key, String iv) {
        final String cipherMode = "AES/CBC/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            byte[] messageByte = decoder.decode(messageBase64);

            //
            byte[] keyByte = key.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            //
            byte[] ivByte = iv.getBytes(charsetName);
            IvParameterSpec ivSpec = new IvParameterSpec(ivByte);

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] content = cipher.doFinal(messageByte);
            String result = new String(content, charsetName);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * AES ECB 加密
     * @param message 需要加密的字符串
     * @param key   密匙
     * @return  返回加密后密文，编码为base64
     */
    public static String encryptECB(String message, String key) {
        final String cipherMode = "AES/ECB/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            byte[] content = new byte[0];
            content = message.getBytes(charsetName);
            //
            byte[] keyByte = key.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] data = cipher.doFinal(content);
            final Base64.Encoder encoder = Base64.getEncoder();
            final String result = encoder.encodeToString(data);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * AES ECB 解密
     * @param messageBase64 密文，base64编码
     * @param key   密匙，和加密时相同
     * @return  解密后数据
     */
    public static String decryptECB(String messageBase64, String key) {
        final String cipherMode = "AES/ECB/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            byte[] messageByte = decoder.decode(messageBase64);

            //
            byte[] keyByte = key.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] content = cipher.doFinal(messageByte);
            String result = new String(content, charsetName);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 测试
     */
    public static void Test()
    {
        String key = "1a2b3c4d5e6f7g8h";
        String iv = "1234567890000000";
        String msg = "Spring";
        {
            String encrypt = AESCryptUtils.encryptCBC(msg, key, iv);
            System.out.println(encrypt);
            String decryptStr = AESCryptUtils.decryptCBC(encrypt, key, iv);
            System.out.println(decryptStr);
        }
        {
            String encrypt = AESCryptUtils.encryptECB(msg, key);
            System.out.println(encrypt);
            String decryptStr = AESCryptUtils.decryptECB(encrypt, key);
            System.out.println(decryptStr);
        }
    }

}

