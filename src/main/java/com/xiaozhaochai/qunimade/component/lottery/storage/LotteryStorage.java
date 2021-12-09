package com.xiaozhaochai.qunimade.component.lottery.storage;


import com.xiaozhaochai.qunimade.component.lottery.LotteryAssistant;
import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;

import java.util.List;

/**
 * 奖池持久化
 * @author Rao
 * @Date 2021/11/16
 **/
public interface LotteryStorage {

    /**
     * 通过key获取奖池
     * @param key 唯一Key
     *            基于存储方式的不同去区分，奖池可能使用不通存储方式，但是需要明确的是，怎么让多个业务的 奖池在存储的Id相同的情况下，能区分。
     * @param lotteryPoolId 奖池ID
     * @return
     */
    LotteryPool getByKeyAndId(String key, String lotteryPoolId);

    /**
     * 持久化奖池
     * @param lotteryPool
     * @return
     */
    boolean storageLottery(LotteryPool lotteryPool);

    /**
     * 获取持久化 key
     * @param key
     * @return
     */
    default String getStorageKey(String key){
        return LotteryAssistant.LOTTERY_PREFIX + key;
    }

    /**
     * 带检查的获取
     */
    LotteryPool getWithCheckByKeyAndId(String key, String lotteryPoolId, int lotteryCount);


    /**
     * 查看奖池有没有东西了
     */
    boolean checkLotteryNotEmpty(String key, int lotteryCount);

    /**
     * 移除某个奖品
     */
    default boolean removeRewardById(String key, String rewardId){
        return this.removeRewardWithCountById( key, rewardId, 1);
    }

    /**
     * 其实一般不会调用该方法
     * @param key
     * @param rewardId
     * @param count
     * @return
     */
    boolean removeRewardWithCountById(String key, String rewardId, Integer count);

    /**
     * 批量移除
     * @param key
     * @param awardIdList
     * @return
     */
    boolean batchRemoveRewardByIds(String key, List<String> awardIdList);

    /**
     * 清除
     * @param lotteryPool
     * @return
     */
    boolean clear(LotteryPool lotteryPool);
}
