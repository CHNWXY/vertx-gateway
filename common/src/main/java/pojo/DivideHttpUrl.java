package pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import constant.ParamKey;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import security.StringUtils;
import constant.Error;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * 对GET请求进行解析，拆分出其中重要的内容；并且贯穿始终
 */
@ToString
@NoArgsConstructor
@Setter
public class DivideHttpUrl implements Serializable {
    static final long serialVersionUID = 19950830L;
    private static final transient Logger LOGGER = Logger.getLogger(DivideHttpUrl.class.getName());
    @JsonIgnore
    transient RoutingContext routingContext;
    @Getter
    transient Company company;


    public DivideHttpUrl(RoutingContext routingContext) {
        this.routingContext = routingContext;
        analysis();
    }

    //请求的真实来源IP
    @Getter
    String xRealIp;
    //签名参数
    @Getter
    String sign;
    //aes加密后的内容
    @Getter
    String dataStr;
    //accessId--->companyId
    @Getter
    String accessId;
    //请求时间件yyyyMMddHHmmssSSS格式
    @Getter
    String reqTime;
    //请求接入密钥md5(accessId+reqTime+accessKey)
    @Getter
    String accessKey;
    //请求方法
    @Getter
    String method;
    //流水
    @Getter
    String msgId;
    //额外需要透传参数
    @Getter
    String extraParams;
    //解密后的参数
    @Getter
    Map<String, Object> data = new TreeMap<>();
    //校验或者解密后的结果
    @Getter
    transient Error error;



    private void analysis() {
        //先拿到IP
        this.xRealIp = getIp();
        //再将鉴权参数全部取出
        this.accessId = getParam(ParamKey.ACCESS_ID);
        this.accessKey = getParam(ParamKey.ACCESS_KEY);
        this.reqTime = getParam(ParamKey.REQ_TIME);
        this.method = getParam(ParamKey.METHOD);
        //将签名字段拿出
        this.sign = getParam(ParamKey.SIGN);
        //将加密字段拿出
        this.dataStr = getParam(ParamKey.DATA_STR);
        //额外的参数拿出
        this.msgId = getParam(ParamKey.MSG_ID);
        this.extraParams = getParam(ParamKey.EXTRA_PARAMS);
        //开始校验
        boolean check = check();
        if (!check) {
            LOGGER.warning("解析并校验失败");
        }
    }

    private String getIp() {
        HttpServerRequest request = routingContext.request();
        //首先获取第一个远程IP
        String remoteIp = request.remoteAddress().host();
        if (!StringUtils.isBlank(remoteIp)) {
            return remoteIp;
        }
        //没有的话就从里面拿一个
        String xRealIp = request.getHeader(ParamKey.X_REAL_IP);
        if (!StringUtils.isBlank(remoteIp)) {
            return xRealIp;
        }
        return null;
    }

    private String getHeader(String key) {
        HttpServerRequest request = routingContext.request();
        return request.getHeader(key);
    }

    private String getParam(String key) {
        HttpServerRequest request = routingContext.request();
        return request.getParam(key);
    }

    private boolean check() {
        //校验请求时间
        if (!checkTime()) {
            error = Error.REQ_TIME_ERR;
            return false;
        }
        //参数签名校验
        if (!checkSign()) {
            error = Error.SIGN_ERR;
            return false;
        }
        //校验access
        if (!checkAccessId()) {
            error = Error.ACCESS_ID_ERR;
            return false;
        }
        //校验密钥
        if (!checkAccessKey()) {
            error = Error.ACCESS_KEY_ERR;
            return false;
        }
        //检查IP白名单
        if (!checkIp()) {
            error = Error.WHITE_IP_ERR;
            return false;
        }
        //检查方法是否拥有
        if (!checkMethod()) {
            error = Error.METHOD_ERR;
            return false;
        }
        //进行解密
        if (!decrypt()) {
            error = Error.AES_DECRYPT_ERR;
            return false;
        }
        //对参数存在性进行校验
        if (!checkParam()) {
            error = Error.PARAM_ERR;
            return false;
        }
        return true;
    }

    private boolean checkAccessId() {
        return true;
    }

    private boolean checkAccessKey() {
        return true;
    }

    private boolean checkTime() {
        return true;
    }

    private boolean checkParam() {
        return true;
    }

    private boolean checkIp() {
        return true;
    }

    private boolean checkMethod() {
        return true;
    }

    private boolean decrypt() {
        return true;
    }

    private boolean checkSign() {
        return true;
    }

}
