package pojo;

import lombok.Data;

import java.util.Set;

@Data
public class Company {
    String id;
    String name;
    String password;
    //用于加密
    String aesKey;
    //用于传输关键信息（所有关键信息都是由我们生成的，所以此处其实是要求对方上传一个公钥）
    String clientRsaPublicKey;
    //用于解密关键信息（冗余字段）
    String clientRsaPrivateKey;
    //用于传输关键信息（这部分是说我们解密他们上传的一些关键信息）
    String serverRsaPublicKey;
    //用于解密关键信息
    String serverRsaPrivateKey;
    //白名单
    Set<AccessIp> accessIpSet;
    //功能
    Set<Ability> abilitySet;

    @Data
    private static class AccessIp {
        String startIp;
        String endId;
        int concurrent;
    }

    @Data
    private static class Ability {
        String method;
        Set<Param> paramSet;
    }

    @Data
    private static class Param {
        String key;
        int type;
        int length;
        long max;
        long min;
        String regex;
    }
}
