package com.xiaozhaochai.qunimade.component.lottery.pool;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.WeightRandom;
import com.xiaozhaochai.qunimade.component.lottery.reward.LotteryReward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽奖池
 *  为什么不用id而还需要使用一个 key？
 *  因为id在数据库下的情况可能会相同，此时则需要定义 带业务标识的 key来区分何种奖池。当你确定你的id能决定在多业务维度下的区分时，我建议你值一样即可。
 * @author Rao
 * @Date 2021/11/16
 **/

public class DefaultLotteryPool implements LotteryPool {

    /**
     * 奖池Key
     *   ： 持久化标记的 key
     */
    private final String key;

    /**
     * 奖池ID
     *   ： 业务id，比如数据库存的
     */
    private final String id;

    /**
     * 奖池
     */
    private final Map<String,Integer> lotteryMap = new HashMap<>();

    /**
     * default
     * @param key
     * @param id
     */
    public DefaultLotteryPool(String key, String id) {
        this.key = key;
        this.id = id;
    }

    /**
     * see top header
     * @param id
     */
    public DefaultLotteryPool(String id) {
        this.id = id;
        this.key = id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean addLottery(String id, Integer count) {
        Assert.notNull( count);
        Assert.isTrue(  count > 0);

        lotteryMap.put(id,lotteryMap.getOrDefault(id,0) + count);
        return count.equals( lotteryMap.get(id));
    }

    @Override
    public boolean addLottery(List<? extends LotteryReward> lotteryRewardList) {
        for (LotteryReward lotteryReward : lotteryRewardList) {
            this.addLottery( lotteryReward.rewardId(),lotteryReward.rewardCount());
        }
        return true;
    }

    @Override
    public boolean addLottery(Map<String, Integer> lotteryMap) {
        this.lotteryMap.putAll( lotteryMap);
        return true;
    }

    @Override
    public Map<String, Integer> getLotteryMap() {
        return lotteryMap;
    }

    @Override
    public List<String> getLotteryList() {
        List<String> allLotteries = new ArrayList<>();

        for (Map.Entry<String, Integer> lotteryEntry : lotteryMap.entrySet()) {
            String id = lotteryEntry.getKey();
            Integer value = lotteryEntry.getValue();
            if( value == 1){
                allLotteries.add(id);
            }
            else{
                for (int i = 0; i <  value;i++) {
                    allLotteries.add(id);
                }
            }
        }
        return allLotteries;
    }

    @Override
    public List<WeightRandom.WeightObj<String>> getWithWeightLotteryList() {

        List<WeightRandom.WeightObj<String>> withWeightLotteryList = new ArrayList<>();
        for (Map.Entry<String, Integer> lotteryEntry : lotteryMap.entrySet()){
            withWeightLotteryList.add( new WeightRandom.WeightObj<>( lotteryEntry.getKey(),lotteryEntry.getValue()));
        }
        return withWeightLotteryList;
    }


}
