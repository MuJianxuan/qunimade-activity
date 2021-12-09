package com.xiaozhaochai.qunimade.component.lottery.mod;

import java.io.Serializable;

/**
 * 1、 添加 锁定义的key数据
 * @author Rao
 * @Date 2021/11/19
 **/
public interface LotteryParams extends Serializable {

    /**
     * 奖池ID
     * @return
     */
    String lotteryPoolId();

    /**
     * 抽奖次数
     * @return
     */
    int lotteryCount();

    /**
     * userId
     * @return
     */
    String userId();

}
