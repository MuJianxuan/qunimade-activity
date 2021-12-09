package com.xiaozhaochai.qunimade.component.lottery.storage;

import com.xiaozhaochai.qunimade.component.lottery.exe.LotteryFailException;
import com.xiaozhaochai.qunimade.component.lottery.pool.DefaultLotteryPool;
import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Redis持久化
 *    (可全局)
 * @author Rao
 * @Date 2021/11/16
 **/
@Slf4j
@Component(value = "redisLotteryStorage")
public class RedisLotteryStorage implements LotteryStorage {

    /**
     * this
     */
    private final RedissonClient redissonClient;

    public RedisLotteryStorage(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取奖池
     * @param key
     * @return
     */
    private RMap<String, Integer> getLotteryMap(String key){
        return redissonClient.getMap( this.getStorageKey( key), JsonJacksonCodec.INSTANCE);
    }

    @Override
    public LotteryPool getByKeyAndId(String key, String lotteryPoolId) {
        DefaultLotteryPool lotteryPool = new DefaultLotteryPool(key, lotteryPoolId);

        RMap<String, Integer> lotteryMap = this.getLotteryMap(key);
        lotteryPool.addLottery(  lotteryMap.readAllMap() );
        return lotteryPool;
    }

    @Override
    public LotteryPool getWithCheckByKeyAndId(String key, String lotteryPoolId, int lotteryCount) {
        this.checkLotteryNotEmpty(key, lotteryCount);
        return this.getByKeyAndId( key,lotteryPoolId);
    }

    @Override
    public boolean storageLottery(LotteryPool lotteryPool) {
        // 区分多业务维度的 key
        String key = lotteryPool.getKey();
        // 存储
        this.getLotteryMap( key).putAll( lotteryPool.getLotteryMap());
        return true;
    }

    @Override
    public boolean checkLotteryNotEmpty(String key, int lotteryCount) {

        if( lotteryCount < 1){
            throw new LotteryFailException();
        }

        int size = this.getLotteryMap( key).size();
        if(size == 0 || size < lotteryCount){
            throw new LotteryFailException();
        }
        return true;
    }

    @Override
    public boolean removeRewardWithCountById(String key, String rewardId, Integer count) {
        if( count == null){
            throw new LotteryFailException();
        }
        RMap<String, Integer> lotteryMap = this.getLotteryMap( key);
        if ( lotteryMap.containsKey( rewardId)) {
            // 减去一个值
            Integer addAndGet = lotteryMap.addAndGet(rewardId, -count);
            // 减少网络调用....
            if (Optional.ofNullable( addAndGet).orElse( 0) == 0) {
                // key 与 value 正确才会 匹配!  理论上是成功的
                lotteryMap.remove( rewardId);
                return true;
            }
            return true;
        }
        throw new LotteryFailException();
    }

    @Override
    public boolean batchRemoveRewardByIds(String key, List<String> awardIdList) {
        awardIdList.forEach( awardId -> this.removeRewardById( key,awardId ));
        return true;
    }

    @Override
    public boolean clear(LotteryPool lotteryPool) {
        String key = lotteryPool.getKey();
        if (this.getLotteryMap( key ).size() > 0) {
            return this.getLotteryMap( key ).delete();
        }
        return true;
    }
}
