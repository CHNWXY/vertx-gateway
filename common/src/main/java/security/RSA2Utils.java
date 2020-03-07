package security;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * 本类进行非对称加密，不推荐使用非对称加密对长字符串进行加密或者解密，徒增资源消耗，另外由于长度限制，过长的字符串的加密和解密会使用循环，对数据分段加密；本类采用的
 * 密钥字符串均为Base64加密后的
 * 另外所有异常都会抛出
 * 下面将会列举几个可以自定义或者暴露出去的接口和参数
 * {@link #IS_LONG_TEXT} 是否否对长文本处理
 * {@link #RESULT_TYPE} 密文结果：1=base64 2=hex
 * {@link #RSA_ALGORITHM} RSA算法
 * {@link #encrypt(String, String)} 加密方法
 * {@link #decrypt(String, String)} 解密方法
 * {@link #getKeyPair} 解密方法
 */
public class RSA2Utils {
    /**
     * 是否对长文本加密；请参照{@link #MAX_DECRYPT_BLOCK}和{@link #MAX_ENCRYPT_BLOCK}
     */
    private static final boolean IS_LONG_TEXT = true;
    /**
     * 结果类型
     */
    private static final int RESULT_TYPE = 2;
    /**
     * RSA 算法
     */
    private static final String RSA_ALGORITHM = "RSA";
    /**
     * 长文本解密块大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    /**
     * 长文本加密块大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * KyeSize
     */
    private static final int KEY_SIZE = 2048;

    /**
     * 加密
     *
     * @param content 待加密的字符串
     * @param pubKey  公钥字符串
     * @return 加密后的文本
     * @throws Exception 异常
     */
    public static String encrypt(String content, String pubKey) throws Exception {
        byte[] data = StringUtils.getBytes(content);
        PublicKey publicKey = string2PubKey(pubKey);
        byte[] resultArr;
        if (IS_LONG_TEXT) {
            resultArr = encryptLongStr(data, publicKey);
        } else {
            resultArr = encrypt(data, publicKey);
        }
        String result;
        switch (RESULT_TYPE) {
            case 1:
                result = Base64Utils.encode(resultArr);
                break;
            case 2:
                result = HexUtils.bytes2Hex(resultArr);
                break;
            default:
                throw new Exception("Unsupport result type");
        }
        return result;
    }

    /**
     * @param content 密文内容
     * @param priKey  私钥
     * @return 解密后的字符串
     * @throws Exception 异常
     */
    public static String decrypt(String content, String priKey) throws Exception {
        byte[] data;
        switch (RESULT_TYPE) {
            case 1:
                data = Base64Utils.decode(content);
                break;
            case 2:
                data = HexUtils.hex2Bytes(content);
                break;
            default:
                throw new Exception("Unsupport result type");
        }
        PrivateKey privateKey = string2PrivateKey(priKey);
        byte[] result;
        if (IS_LONG_TEXT) {
            result = decryptLongStr(data, privateKey);
        } else {
            result = decrypt(privateKey, data);
        }
        return StringUtils.bytes2String(result);
    }

    /**
     * 响应公私钥对
     *
     * @return 0号 公钥 1号 私钥
     * @throws NoSuchAlgorithmException 异常
     */
    public static List<String> getKeyPair() throws NoSuchAlgorithmException {
        KeyPair keyPairObj = getKeyPairObj();
        return List.of(Base64Utils.encode(keyPairObj.getPublic().getEncoded()), Base64Utils.encode(keyPairObj.getPrivate().getEncoded()));
    }

    /**
     * 将公钥字符串转化为对象
     *
     * @param s base64字符串
     * @return 公钥
     * @throws NoSuchAlgorithmException     异常
     * @throws UnsupportedEncodingException 异常
     * @throws InvalidKeySpecException      异常
     */
    private static PublicKey string2PubKey(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Utils.decode(s));
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 对段字符串进行加密
     *
     * @param bytes     字节数组
     * @param publicKey 公钥
     * @return 加密后的数组
     * @throws InvalidKeyException       异常
     * @throws BadPaddingException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws NoSuchPaddingException    异常
     * @throws NoSuchAlgorithmException  异常
     */
    private static byte[] encrypt(byte[] bytes, PublicKey publicKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(bytes);
    }

    /**
     * 对长字符串进行加密
     *
     * @param bytes     字节数组
     * @param publicKey 公钥
     * @return 加密后的数组
     * @throws NoSuchPaddingException   异常
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException      异常
     */
    private static byte[] encryptLongStr(byte[] bytes, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = bytes.length;
        byte[] encryptedData = new byte[0];
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            encryptedData = out.toByteArray();
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    /**
     * 私钥字符串转为私钥对象
     *
     * @param priStr 私钥字符串
     * @return 私钥对象
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeySpecException  异常
     */
    private static PrivateKey string2PrivateKey(String priStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(priStr));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解密
     *
     * @param privateKey 私钥
     * @param bytes      字节数组
     * @return 解密后的字节数组
     * @throws NoSuchPaddingException    异常
     * @throws NoSuchAlgorithmException  异常
     * @throws BadPaddingException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws InvalidKeyException       异常
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(bytes);
    }

    /**
     * 解密
     *
     * @param data       解密前的字节数组
     * @param privateKey 私钥
     * @return 解密后的字节数组
     * @throws InvalidKeyException      异常
     * @throws NoSuchPaddingException   异常
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] decryptLongStr(byte[] data, PrivateKey privateKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = data.length;
        byte[] result = new byte[0];
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            result = out.toByteArray();
        } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得一堆公私钥
     *
     * @return KeyPair对象
     * @throws NoSuchAlgorithmException 异常
     */
    private static KeyPair getKeyPairObj() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(StringUtils.getBytes(String.valueOf(System.currentTimeMillis())));
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        return keyPairGenerator.genKeyPair();
    }
}
