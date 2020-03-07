package security;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 将Base64字符串和字节数组互转
 */
public class Base64Utils {

    /**
     * 将字节数组编码为base64字符串
     *
     * @param b 字节数组
     * @return String 字符串
     */
    public static String encode(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     * 将字符串转base64为字节数组
     *
     * @param s 字符串
     * @return byte[] 字节数组
     */
    public static byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }

}
