package pojo;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class CenterConfig {
    int httpServerPort;
    int settingServerPort;

    public static CenterConfig centerConfig() {
        CenterConfig centerConfig = new CenterConfig();
        centerConfig.httpServerPort = 9001;
        centerConfig.settingServerPort = 9002;
        return centerConfig;
    }
}
