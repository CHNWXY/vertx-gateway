import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import org.junit.Test;
import pojo.DivideHttpUrl;

import java.util.logging.Logger;

public class TestLog {
    private static final Logger LOGGER = Logger.getLogger(TestLog.class.getName());

    @Test
    public void log() {
        LOGGER.info("MSG");
        LOGGER.info("MSG21");
        LOGGER.info("MSG2");
        LOGGER.warning("MSG2");
        LOGGER.severe("MSG_OK");
    }

    @Test
    public void testTcpServer() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(h -> {
            h.handler(buffer -> {
//                LOGGER.info(DivideHttpUrl.fromBytes(buffer.getBytes()).toString());

            });
            while (true) {
                h.write("I Get It");
            }
        }).listen(8080, "localhost", h -> {
            if (h.succeeded()) {
                LOGGER.info("监听成功");
            }
        });
        while (true) {
            Thread.sleep(1000);
//            LOGGER.info("监听中...");
        }
    }

    @Test
    public void testTcpClient() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        netClient.connect(8080, "localhost", h -> {
//            h.result().write(Buffer.buffer(new DivideHttpUrl().toBytes()));
            LOGGER.info("发送完成");
            h.result().handler(read -> {
                LOGGER.info("收到回复：" + read.toString());
            });
        });
        while (true) {
            Thread.sleep(1000);
        }
    }
}
