package tcp;

import config.CompanyReloadSetting;
import config.RollIn;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import pojo.CenterConfig;

import java.util.logging.Logger;

public class Center {
    private static final Logger LOGGER = Logger.getLogger(Center.class.getName());
    private static final Vertx VERTX = Vertx.vertx();

    public static void main(String[] args) {
        CenterConfig centerConfig = CenterConfig.centerConfig();
        //监听端口，监听来自Portal和Router的沟通请求
        NetServer http = VERTX.createNetServer();
        http.connectHandler(coon -> {
            coon.handler(accept -> {
                LOGGER.info(accept.toString());
            });
        }).listen(centerConfig.getHttpServerPort(), h -> {
            if (h.succeeded()) {
                LOGGER.info("监听服务端口成功");
            } else {
                LOGGER.severe("监听服务端口失败");
            }
        });
        //和Portal和Router建立联系
        NetServer setting = VERTX.createNetServer();
        setting.connectHandler(coon -> {
            coon.handler(buffer -> {
                LOGGER.info(buffer.toString());
                coon.write(new CompanyReloadSetting(new RollIn()).toBuffer());
            });
        }).listen(centerConfig.getSettingServerPort(), h -> {
            if (h.succeeded()) {
                LOGGER.info("监听配置端口成功");
            } else {
                LOGGER.severe("监听配置端口失败");
            }
        });
    }
}
