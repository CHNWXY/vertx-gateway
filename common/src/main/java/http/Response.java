package http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import constant.Error;
import constant.ParamKey;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import pojo.DivideHttpUrl;
import security.MainSecurity;
import security.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@Accessors(chain = true)
public class Response implements Serializable {
    static final long serialVersionUID = 19941215L;
    int result;
    int errorCode;
    String dataStr = "";
    Map<String, Object> data = new HashMap<>();
    transient JsonObject resp = new JsonObject();
    @Setter
    @JsonIgnore
    transient RoutingContext routingContext;
    DivideHttpUrl divideHttpUrl;

    public static Response make(RoutingContext routingContext, DivideHttpUrl divideHttpUrl) {
        Response response = new Response();
        response.routingContext = routingContext;
        response.divideHttpUrl = divideHttpUrl;
        return response;
    }

    public void addSuccess(String k, String v) {
        this.data.put(k, v);
    }

    public void success() {
        if (!this.data.isEmpty()) {
            this.dataStr = MainSecurity.aesEncrypt(this.data.toString(), divideHttpUrl.getCompany().getAesKey());
        }
        end();
    }

    public void error(Error error) {
        result = error.getCode();
        end();
    }

    public void fail(int errorCode) {
        result = Error.ERROR.getCode();
        this.errorCode = errorCode;
        end();
    }

    private void end() {
        HttpServerResponse response = routingContext.response();
        if (!response.ended()) {
            if (!StringUtils.isBlank(divideHttpUrl.getMsgId())) {
                resp.put(ParamKey.MSG_ID, divideHttpUrl.getMsgId());
            }
            if (!StringUtils.isBlank(divideHttpUrl.getExtraParams())) {
                resp.put(ParamKey.EXTRA_PARAMS, divideHttpUrl.getExtraParams());
            }
            resp.put(ParamKey.ERROR_CODE, errorCode);
            resp.put(ParamKey.RESULT, result);
            resp.put(ParamKey.DATA_STR, dataStr);
            response.end(resp.toBuffer());
        }
    }


}
