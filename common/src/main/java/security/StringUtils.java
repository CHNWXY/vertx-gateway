package security;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    /**
     * 编码方法
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * 根据本类的常量对字符串进行获取字节数组的操作
     *
     * @param s String:待处理的字符串
     * @return byte[] 形成的字节数组
     */
    public static byte[] getBytes(String s) {
        return s.getBytes(CHARSET);
    }

    /**
     * 将字节数组转为文本
     *
     * @param data 字节数组
     * @return 文本
     */
    public static String bytes2String(byte[] data) {
        return new String(data, CHARSET);
    }


    /**
     * 字符串是不是空的
     *
     * @param str 字符串
     * @return 结果
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

}
