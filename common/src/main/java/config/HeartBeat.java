package config;

import constant.ParamKey;
import constant.TcpPrefix;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;
import utils.Bytes;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class HeartBeat implements Serializable {
    static final long serialVersionUID = 20200202L;
    RollIn rollIn;

    public HeartBeat(RollIn rollIn) {
        this.rollIn = rollIn;
    }

    public Buffer toBuffer() {
        JsonObject o = new JsonObject();
        o.put(ParamKey.TCP_PREFIX, TcpPrefix.HEART_BEAT);
        o.put(ParamKey.SETTING_CONTENT, Bytes.toBytes(this));
        return o.toBuffer();
    }
}
