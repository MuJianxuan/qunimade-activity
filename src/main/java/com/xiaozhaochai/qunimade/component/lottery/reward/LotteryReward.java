package com.xiaozhaochai.qunimade.component.lottery.reward;

/**
 * 抽奖奖励
 * @author Rao
 * @Date 2021/11/16
 **/
public interface LotteryReward {

    /**
     * 获取奖励ID
     * @return
     */
    String rewardId();

    /**
     * 奖励数
     * @return
     */
    Integer rewardCount();

}
