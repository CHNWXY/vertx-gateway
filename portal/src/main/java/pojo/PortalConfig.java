package pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PortalConfig {
    int serverPort;
    String path;

    String centerHost;
    int centerPort;
    int settingPort;

    private PortalConfig() {
    }

    public static PortalConfig portalConfig() {
        return new PortalConfig()
                .setPath("/api").setServerPort(8080)
                .setCenterHost("localhost")
                .setCenterPort(9001)
                .setSettingPort(9002);
    }
}
