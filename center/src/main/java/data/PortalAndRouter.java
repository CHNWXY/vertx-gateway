package data;

import config.RollIn;

import javax.sound.sampled.Port;
import java.util.HashSet;
import java.util.Set;

/**
 * 存储Portal的列表和Router的列表
 */
public class PortalAndRouter {
    private volatile static Set<RollIn> ROLL_IN_SET = new HashSet<>();

    /**
     * 写入Portal和Router
     *
     * @param rollIn
     */
    public synchronized static void push(RollIn rollIn) {
        ROLL_IN_SET.add(rollIn);
    }

    /**
     * 获取Portal
     *
     * @param id
     * @return
     */
    public synchronized static RollIn offerPortal(int id) {
        return offer(0, id);
    }

    public synchronized static RollIn offerRouter(int id) {
        return offer(2, id);
    }

    private synchronized static RollIn offer(int type, int id) {
        for (RollIn roll : ROLL_IN_SET) {
            if (roll.getType() == type && roll.getId() == id) {
                return roll;
            }
        }
        return null;
    }
}
