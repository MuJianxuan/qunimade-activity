package com.xiaozhaochai.qunimade.component.lottery.lotterystrategy;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.RandomUtil;
import com.xiaozhaochai.qunimade.component.lottery.exe.LotteryFailException;
import com.xiaozhaochai.qunimade.component.lottery.pool.DefaultLotteryPool;
import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 带权重的抽奖策略
 * @author Rao
 * @Date 2021/11/16
 **/
@Component(value = "withWeightLotteryStrategy")
public class WithWeightLotteryStrategy implements LotteryStrategy {

    /**
     * 单抽
     * @param lotteryPool
     * @return
     */
    @Override
    public String sweepStake(LotteryPool lotteryPool) {
        List<WeightRandom.WeightObj<String>> withWeightLotteryList = lotteryPool.getWithWeightLotteryList();
        WeightRandom<String> weightRandom = RandomUtil.weightRandom(withWeightLotteryList);
        return weightRandom.next();
    }

    /**
     * 移除式 抽奖策略
     * @see WithWeightLotteryStrategy#testRemovableSweepStake()
     * @param lotteryPool
     * @param count
     * @return
     */
    @Override
    public List<String> removableSweepStake(LotteryPool lotteryPool, int count) {
        if( count == 1){
            List<WeightRandom.WeightObj<String>> withWeightLotteryList = lotteryPool.getWithWeightLotteryList();
            WeightRandom<String> weightRandom = RandomUtil.weightRandom(withWeightLotteryList);
            return Collections.singletonList( weightRandom.next() );
        }
        // 移除式抽奖  一个宝箱有多个奖励，不能得到多了
        List<String> result = new ArrayList<>();
        Map<String, Integer> lotteryMap = lotteryPool.getLotteryMap();
        for (int i = 0; i < count; i++) {
            List<WeightRandom.WeightObj<String>> withWeightLotteryList = new ArrayList<>();
            for (Map.Entry<String, Integer> lotteryEntry : lotteryMap.entrySet()){
                withWeightLotteryList.add( new WeightRandom.WeightObj<>( lotteryEntry.getKey(),lotteryEntry.getValue()));
            }
            WeightRandom<String> weightRandom = RandomUtil.weightRandom(withWeightLotteryList);
            String next = weightRandom.next();
            result.add( next );

            // 礼物值
            int value = Optional.ofNullable(lotteryMap.get(next)).orElseThrow(LotteryFailException::new) - 1;
            if(value == 0){
                lotteryMap.remove( next);
            }
            else{
                lotteryMap.put(next, value );
            }
        }
        return result;

    }

    @Override
    public List<String> nonRemovableSweepStake(LotteryPool lotteryPool, int count) {
        List<WeightRandom.WeightObj<String>> withWeightLotteryList = lotteryPool.getWithWeightLotteryList();
        WeightRandom<String> weightRandom = RandomUtil.weightRandom(withWeightLotteryList);
        if( count == 1){
            return Collections.singletonList( weightRandom.next() );
        }

        // 抽多次
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(  weightRandom.next() );
        }
        return result;
    }


    /**
     * 测试方法
     */
    public static void testRemovableSweepStake() {
        LotteryStrategy lotteryStrategy = new WithWeightLotteryStrategy();
        for (int i = 0; i < 100000; i++) {
            DefaultLotteryPool defaultLotteryPool = new DefaultLotteryPool("1");
            defaultLotteryPool.addLottery("1",3);
            defaultLotteryPool.addLottery("2",2);
            List<String> idList = lotteryStrategy.removableSweepStake(defaultLotteryPool, 5);
            long count = idList.stream().filter("1"::equals).count();
            assert count == 3;
        }
    }

}
