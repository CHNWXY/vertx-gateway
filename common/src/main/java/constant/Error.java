package constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Error {
    ERROR(999, "失败"),
    SYS_ERR(998, "系统异常"),
    REQ_TIME_ERR(101, "请求时间异常"),
    ACCESS_ID_ERR(102, "请求接入ID异常"),
    ACCESS_KEY_ERR(103, "请求接入密钥异常"),
    WHITE_IP_ERR(104, "IP未授权"),
    METHOD_ERR(105, "未授权的功能"),
    AES_DECRYPT_ERR(106, "对参数解密失败"),
    PARAM_ERR(107, "参数缺失或者不符合参数要求"),
    SIGN_ERR(108, "参数签名不正确"),
    ;

    int code;
    String msg;

}
