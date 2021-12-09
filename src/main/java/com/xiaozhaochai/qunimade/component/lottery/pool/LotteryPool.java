package com.xiaozhaochai.qunimade.component.lottery.pool;

import cn.hutool.core.lang.WeightRandom;
import com.xiaozhaochai.qunimade.component.lottery.reward.LotteryReward;

import java.util.List;
import java.util.Map;

/**
 * 仅是一个表达 奖池 这么一个领域模型的概念
 * @author Rao
 * @Date 2021/11/16
 **/
public interface LotteryPool {

    /**
     * 奖池Key
     * @return
     */
    String getKey();

    /**
     * 获取奖池ID
     * @return
     */
    String getId();

    /**
     * 添加奖品
     * @param id
     * @return
     */
    default boolean addLottery(String id){
        return this.addLottery(id, 1);
    }

    /**
     * 添加奖品
     * @param id
     * @return
     */
    boolean addLottery(String id, Integer count);

    /**
     * 添加奖品
     * @param lotteryRewardList
     * @return
     */
    boolean addLottery(List<? extends LotteryReward> lotteryRewardList);

    /**
     * 奖池
     * @param lotteryMap
     * @return
     */
    boolean addLottery(Map<String, Integer> lotteryMap);

    //========================= end

    /**
     * 获取 奖品 Map
     * @return
     */
    Map<String,Integer> getLotteryMap();

    /**
     * 获取现在所有的奖品
     */
    List<String> getLotteryList();

    /**
     * 获取带权重的所有奖品
     * @return
     */
    List<WeightRandom.WeightObj<String>> getWithWeightLotteryList();

}
