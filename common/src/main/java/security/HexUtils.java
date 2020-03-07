package security;

/**
 * 将字节数组和HEX字符串进行互转
 */
public class HexUtils {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 字节数组转字符串
     *
     * @param byteArr 字节数组
     * @return 字符串
     */
    public static String bytes2Hex(byte[] byteArr) {
        StringBuilder sb = new StringBuilder(byteArr.length);
        for (byte b : byteArr) {
            String sTemp = Integer.toHexString(255 & b);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * HEX字符串转字节数组
     *
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] hex2Bytes(String str) {
        if (str == null) {
            return null;
        } else {
            char[] hex = str.toCharArray();
            int length = hex.length / 2;
            byte[] raw = new byte[length];

            for (int i = 0; i < length; ++i) {
                int high = Character.digit(hex[i * 2], 16);
                int low = Character.digit(hex[i * 2 + 1], 16);
                int value = high << 4 | low;
                if (value > 127) {
                    value -= 256;
                }
                raw[i] = (byte) value;
            }
            return raw;
        }
    }
}
