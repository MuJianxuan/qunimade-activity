package com.xiaozhaochai.qunimade.component.lottery.lotterystrategy;


import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;

import java.util.List;

/**
 * 抽奖策略
 * 1、抽单次
 * 2、非移除式抽多次
 * 3、需移除式抽多次
 * @author Rao
 * @Date 2021/11/16
 **/
public interface LotteryStrategy {


    /**
     * 单抽
     */
    String sweepStake(LotteryPool lotteryPool);

    /**
     * 移除式抽奖 （感觉这种不怎么用到）
     * @param lotteryPool
     * @param count
     * @return
     */
    List<String> removableSweepStake(LotteryPool lotteryPool, int count);

    /**
     * 非移除式抽奖
     */
    List<String> nonRemovableSweepStake(LotteryPool lotteryPool, int count);

}
