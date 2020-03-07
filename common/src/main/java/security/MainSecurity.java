package security;

import java.util.logging.Logger;

/**
 * 本项目中真正对外提供服务的工具类
 */
public class MainSecurity {
    private static final Logger LOGGER = Logger.getLogger(MainSecurity.class.getName());

    /**
     * 加密，当失败的时候，返回空字符串
     *
     * @param content
     * @param pubKey
     * @return
     */
    public static String rsaEncrypt(String content, String pubKey) {
        try {
            return RSA2Utils.encrypt(content, pubKey);
        } catch (Exception e) {
            LOGGER.info("RSA加密失败");
            e.printStackTrace();
            return null;
        }
    }

    public static String rsaDecrypt(String content, String priKey) {
        try {
            return RSA2Utils.decrypt(content, priKey);
        } catch (Exception e) {
            LOGGER.info("RSA解密失败");
            e.printStackTrace();
            return null;
        }
    }

    public static String aesEncrypt(String content, String key) {
        try {
            return AesUtils.encrypt(content, key);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("AES加密失败");
            return null;
        }
    }

    public static String aesDecrypt(String content, String key) {
        try {
            return AesUtils.decrypt(content, key);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("AES解密失败");
            return null;
        }
    }

    public static String sign(String content) {
        try {
            return MessageDigest.md5(content);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("MD5加密失败");
            return null;
        }
    }
}
