package http;

import config.HeartBeat;
import config.RollIn;
import constant.Error;
import constant.ParamKey;
import constant.TcpPrefix;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.Router;
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
        RollIn rollIn = RollIn.portal();
        //创建TCP监听
        NetClient httpNetClient = VERTX.createNetClient();
        httpNetClient.connect(portalConfig.getCenterPort(), portalConfig.getCenterHost(), socket -> {
            if (socket.succeeded()) {
                //获取Socket
                NetSocket netSocket = socket.result();
                //创建HTTP监听
                HttpServer server = VERTX.createHttpServer();
                Router router = Router.router(VERTX);
                //监听HTTP端口
                server.requestHandler(router).listen(portalConfig.getServerPort(), h -> {
                    if (h.succeeded()) {
                        LOGGER.info("HTTP端口监听成功");
                    } else {
                        LOGGER.severe("HTTP端口监听失败");
                    }
                });
                //对应监听对应的URL
                router.route(portalConfig.getPath()).handler(http -> {
                    DivideHttpUrl divideHttpUrl = new DivideHttpUrl(http);
                    Response response = Response.make(http, divideHttpUrl);
                    if (divideHttpUrl.getError() == null) {
                        next(netSocket, response);
                    } else {
                        response.error(divideHttpUrl.getError());
                    }
                    //处理响应
                    netSocket.handler(resp -> {
                        //这部分是后面处理成功之后的响应
                        Response respObj = (Response) Bytes.fromBytes(resp.getBytes());
                        if (respObj == null) {
                            response.error(Error.SYS_ERR);
                        } else {
                            respObj.success();
                        }
                    });
                });

            } else {
                LOGGER.severe("和Center建立TCP连接失败");
            }
        });
        //创建配置监听
        NetClient netClient = VERTX.createNetClient();
        netClient.connect(portalConfig.getSettingPort(), portalConfig.getCenterHost(), h -> {
            if (h.succeeded()) {
                NetSocket netSocket = h.result();
                //发送第一个身份识别
                netSocket.write(rollIn.toBuffer());
                //持续发送心跳
                VERTX.setPeriodic(5000, heart -> {
                    netSocket.write(new HeartBeat(rollIn).toBuffer());
                });
                //监听配置
                netSocket.handler(receive -> {
                    JsonObject receiveJson = receive.toJsonObject();
                    String tcpPrefix = receiveJson.getString(ParamKey.TCP_PREFIX);
                    switch (tcpPrefix) {
                        case TcpPrefix.RELOAD_A_COMPANY:
                            LOGGER.info("准备开始重新加载公司");
                            break;
                        default:
                            break;
                    }
                });
            } else {
                LOGGER.severe("创建服务配置连接失败");
            }
        });

    }

    private static void next(NetSocket netSocket, Response response) {
        LOGGER.info("Portal校验通过，开始下发");
        netSocket.write(Buffer.buffer().appendBytes(Bytes.toBytes(response)));
    }
}
