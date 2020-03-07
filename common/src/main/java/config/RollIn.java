package config;

import constant.ParamKey;
import constant.ProgramType;
import constant.TcpPrefix;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;
import utils.Bytes;

import java.io.Serializable;

//接入
@Data
@Accessors(chain = true)
public class RollIn implements Serializable {
    static final long serialVersionUID = 20010101L;
    //区分Portal和Router
    int type;
    //ID
    int id;

    public static RollIn portal() {
        return new RollIn().setType(ProgramType.PORTAL);
    }

    public Buffer toBuffer() {
        JsonObject object = new JsonObject();
        object.put(ParamKey.TCP_PREFIX, TcpPrefix.ROLL_IN_SYSTEM);
        object.put(ParamKey.SETTING_CONTENT, Bytes.toBytes(this));
        return object.toBuffer();
    }
}
