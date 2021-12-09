package com.xiaozhaochai.qunimade.component.lottery;



import com.xiaozhaochai.qunimade.component.lottery.mod.LotteryParams;
import com.xiaozhaochai.qunimade.component.lottery.reward.LotteryReward;

import java.util.List;

/**
 * 抽奖助手
 *  1、初始化奖池  持久化奖池入口
 *  2、抽奖入口   池子ID
 *  >> Rao
 *  关于抽奖设计
 *   1、需排队的抽奖接口
 *   2、无需排队的抽奖接口
 * @author Rao
 * @Date 2021/11/16
 **/
public interface LotteryAssistant<R,P extends LotteryParams> {

    // ================ constants=====================

    /**
     * 持久化： 奖品池 key
     */
    String LOTTERY_PREFIX = "lottery:";

    /**
     * 奖池分布式锁 Key
     */
    String LOTTERY_LOCK_KEY = "lottery_lock:";

    /**
     * 池子全局锁key
     * @param key 池子的key
     * @return
     */
    default String buildLockLotteryGlobalKey(String key){
        return LotteryAssistant.LOTTERY_LOCK_KEY + key;
    }
    // ================ constants=====================

    /**
     * 排队式抽奖方法, 意味着会移除奖池的奖励
     *   with lock
     */
    R removableSweepStake(P params);

    /**
     * 非排队式抽奖方法，意味着不会移除奖池的奖励
     *   no lock
     */
    R nonRemovableSweepStake(P params);

    /**
     *  初始化奖池
     */
    boolean initLotteryPool(String lotteryPoolId, List<? extends LotteryReward> lotteryRewardList);

}
