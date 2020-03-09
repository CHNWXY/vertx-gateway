package tcp;

import config.CompanyReloadSetting;
import config.HeartBeat;
import config.RollIn;
import constant.ParamKey;
import constant.TcpPrefix;
import data.PortalAndRouter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pojo.CenterConfig;
import utils.Bytes;

import java.net.http.HttpResponse;
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
        //创建一个HTTP接口，提供给portal和router进行注册和心跳检测
        HttpServer register = VERTX.createHttpServer();
        Router router = Router.router(VERTX);
        register.requestHandler(router).listen(centerConfig.getSettingServerPort(), h -> {
            if (h.succeeded()) {
                LOGGER.info("注册配置端口成功:" + centerConfig.getSettingServerPort());
            } else {
                LOGGER.severe("注册配置端口失败:{}" + h.cause().getLocalizedMessage());
            }
        });
        router.route().handler(Center::settings);
    }

    private static void settings(RoutingContext routingContext) {

        LOGGER.info("收到配置注册信息");
        routingContext.request().bodyHandler(buffer -> {
            try {
                //拿到之后 判断功能 区分转为不同的类，然后交由不同的类去处理
                String tcpPrefix = buffer.toJsonObject().getString(ParamKey.TCP_PREFIX);
                JsonObject settings = buffer.toJsonObject().getJsonObject(ParamKey.SETTING_CONTENT);
                //是空直接抛弃
                if (settings == null || tcpPrefix == null) {
                    return;
                }
                switch (tcpPrefix) {
                    case TcpPrefix
                            .ROLL_IN_SYSTEM:
                        RollIn rollIn = settings.mapTo(RollIn.class);
                        register(rollIn);
                        break;
                    case TcpPrefix.HEART_BEAT:
                        HeartBeat heartBeat = settings.mapTo(HeartBeat.class);
                        heartBeat(heartBeat);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            routingContext.response().end();
        });
    }

    private static void register(RollIn rollIn) {
        LOGGER.info("有人注册:" + rollIn.toString());
        PortalAndRouter.push(rollIn);
    }

    private static void heartBeat(HeartBeat heartBeat) {
        LOGGER.info("心跳检测:" + heartBeat.toString());
    }
}
