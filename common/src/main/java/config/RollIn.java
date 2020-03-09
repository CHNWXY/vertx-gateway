package config;

import constant.ParamKey;
import constant.ProgramType;
import constant.TcpPrefix;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import utils.Bytes;

import java.io.Serializable;

//接入
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RollIn implements Serializable {
    static final long serialVersionUID = 20010101L;
    //区分Portal和Router
    int type;
    //ID
    int id;
    //设置地址
    String host;
    //设置端口
    int port;

    public static RollIn portal() {
        return new RollIn().setType(ProgramType.PORTAL);
    }

    public Buffer toBuffer() {
        JsonObject object = new JsonObject();
        object.put(ParamKey.TCP_PREFIX, TcpPrefix.ROLL_IN_SYSTEM);
        object.put(ParamKey.SETTING_CONTENT, JsonObject.mapFrom(this));
        return object.toBuffer();
    }
}
