package http;

import config.HeartBeat;
import config.RollIn;
import constant.Error;
import constant.ParamKey;
import constant.TcpPrefix;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import pojo.DivideHttpUrl;
import pojo.PortalConfig;
import utils.Bytes;

import java.util.logging.Logger;

/**
 * 负责进行参数校验和健全，最终将所有的内容序列化之后传递给后台
 */
public class Accept {
    public static final Vertx VERTX = Vertx.vertx();

    private static final Logger LOGGER = Logger.getLogger(Accept.class.getName());

    public static void main(String[] args) {
        //读取配置文件
        PortalConfig portalConfig = PortalConfig.portalConfig();
        //RollIn
        RollIn rollIn = RollIn.portal().setHost("localhost").setPort(portalConfig.getSettingPort());


        //创建HTTP监听
        HttpServer server = VERTX.createHttpServer();
        Router mainHttpRouter = Router.router(VERTX);
        //监听HTTP端口
        server.requestHandler(mainHttpRouter).listen(portalConfig.getServerPort(), h -> {
            if (h.succeeded()) {
                LOGGER.info("HTTP端口监听成功");
            } else {
                LOGGER.severe("HTTP端口监听失败");
            }
        });
        WebClient mainWebClient = WebClient.create(VERTX);
        //对应监听对应的URL
        mainHttpRouter.route(portalConfig.getPath()).handler(http -> {
            DivideHttpUrl divideHttpUrl = new DivideHttpUrl(http);
            Response response = Response.make(http, divideHttpUrl);
            //判断是否有错误
            if (divideHttpUrl.getError() == null) {
                next(mainWebClient, response).onSuccess(resp -> {
                    //处理响应
                    //这部分是后面处理成功之后的响应
                    LOGGER.info("收到响应");
                    try {
                        if (resp == null) {
                            response.error(Error.SYS_ERR);
                        } else {
                            resp.setRoutingContext(http).success();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.error(Error.SYS_ERR);
                    }
                }).onFailure(f -> {
                    f.printStackTrace();
                    response.error(Error.SYS_ERR);
                });
            } else {
                response.error(divideHttpUrl.getError());
            }


        });
        //创建配置监听
        HttpServer settingServer = VERTX.createHttpServer();
        Router settingRouter = Router.router(VERTX);
        settingServer.requestHandler(settingRouter).listen(portalConfig.getSettingPort(), h -> {
            if (h.succeeded()) {
                LOGGER.info("创建配置监听成功");
            } else {
                LOGGER.severe("创建配置监听失败");
            }
        });
        settingRouter.route().handler(h -> {
        });

        //发送注册和心跳
        WebClient webClient = WebClient.create(VERTX);
        //注册
        sendHttp(portalConfig, rollIn.toBuffer(), webClient);
        //发送心跳
        VERTX.setPeriodic(5000, heartBeat -> {
            sendHttp(portalConfig, new HeartBeat(rollIn).toBuffer(), webClient);
        });
    }

    private static Future<Response> next(WebClient webClient, Response response) {
        return Future.future(p -> {
            webClient.postAbs("/").sendJson(JsonObject.mapFrom(response), h -> {
                if (h.succeeded()) {
                    p.complete(h.result().bodyAsJsonObject().mapTo(Response.class));
                } else {
                    p.fail(h.cause());
                }
            });
        });
    }

    private static void sendHttp(PortalConfig portalConfig, Buffer buffer, WebClient webClient) {
        String url = "http://" + portalConfig.getCenterHost() + ":" + portalConfig.getCenterSettingPort() + "/";
        LOGGER.info(url + "---" + buffer.toJsonObject());
        webClient.postAbs(url)
                .sendBuffer(buffer, h -> {
                    if (h.succeeded()) {
                        LOGGER.info("发送成功！！！");
                    } else {
                        LOGGER.severe("发送失败！！！");
                    }
                });
    }
}
