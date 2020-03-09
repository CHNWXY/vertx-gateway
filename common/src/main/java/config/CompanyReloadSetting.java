package config;

import constant.ParamKey;
import constant.TcpPrefix;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;
import pojo.Company;
import utils.Bytes;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CompanyReloadSetting implements Serializable {
    static final long serialVersionUID = 20100101L;
    RollIn rollIn;
    Company company;

    public CompanyReloadSetting(RollIn rollIn) {
        this.rollIn = rollIn;
    }

    public Buffer toBuffer() {
        JsonObject object = new JsonObject();
        object.put(ParamKey.TCP_PREFIX, TcpPrefix.RELOAD_A_COMPANY);
        object.put(ParamKey.SETTING_CONTENT, Bytes.toBytes(this));
        return object.toBuffer();
    }
}
