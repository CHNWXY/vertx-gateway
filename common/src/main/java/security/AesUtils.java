package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 本类采用对称加密 加密算法：{@link #PADDINIG_MODE}
 * {@link #RESULT_TYPE} 密文结果：1=base64 2=hex
 */
public class AesUtils {
    /**
     * 整平方法
     */
    private static final int RESULT_TYPE = 2;

    /**
     * 加密方法
     */
    private static final String AES_ALGORITHM = "AES";

    /**
     * 填充方法
     */
    private static final String PADDINIG_MODE = "AES/CBC/PKCS5Padding";

    /**
     * 偏移量
     */
    private static final byte[] IV = "0000000000000000".getBytes();

    public static String encrypt(String s, String k) throws Exception {
        SecretKeySpec key = new SecretKeySpec(StringUtils.getBytes(k), AES_ALGORITHM);
        byte[] data = encrypt(StringUtils.getBytes(s), key);
        String result;
        switch (RESULT_TYPE) {
            case 1:
                result = Base64Utils.encode(data);
                break;
            case 2:
                result = HexUtils.bytes2Hex(data);
                break;
            default:
                throw new Exception("Unsupport Result Type");
        }
        return result;
    }

    public static String decrypt(String s, String k) throws Exception {
        SecretKeySpec key = new SecretKeySpec(StringUtils.getBytes(k), AES_ALGORITHM);
        byte[] data;
        switch (RESULT_TYPE) {
            case 1:
                data = Base64Utils.decode(s);
                break;
            case 2:
                data = HexUtils.hex2Bytes(s);
                break;
            default:
                throw new Exception("Unsupport Result Type");
        }
        return StringUtils.bytes2String(decrypt(data, key));
    }

    private static byte[] encrypt(byte[] data, SecretKeySpec keySpec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec ivspec = new IvParameterSpec(IV);
        Cipher cipher = Cipher.getInstance(PADDINIG_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data, SecretKeySpec keySpec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec ivspec = new IvParameterSpec(IV);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);
        return cipher.doFinal(data);
    }
}
