package security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

public class MessageDigest {

    public static String md5(String text) throws NoSuchAlgorithmException {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("md5");
        byte[] buffer = digest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : buffer) {
            int a = b & 0xff;
            String hex = Integer.toHexString(a);

            if (hex.length() == 1) {
                hex = 0 + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static String hmacSha1(String data, String key, int type) throws Exception {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        //用给定密钥初始化 Mac 对象
        mac.init(signinKey);
        //完成 Mac 操作
        byte[] rawHmac = mac.doFinal(StringUtils.getBytes(data));
        String result;
        switch (type) {
            case 1:
                result = Base64Utils.encode(rawHmac);
                break;
            case 2:
                result = HexUtils.bytes2Hex(rawHmac);
                break;
            default:
                throw new Exception("Unsupport Type");
        }
        return result;
    }
}