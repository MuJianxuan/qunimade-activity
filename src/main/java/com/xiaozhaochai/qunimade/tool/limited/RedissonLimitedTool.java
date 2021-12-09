package com.xiaozhaochai.qunimade.tool.limited;

import com.xiaozhaochai.qunimade.tool.limited.mod.LimitWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;

import java.util.function.Function;

/**
 * 限量工具
 *  1、天限量/总限量
 *
 * @author Rao
 * @Date 2021/12/03
 **/
@Slf4j
public class RedissonLimitedTool {

    private final RedissonClient redissonClient;

    public RedissonLimitedTool(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 限量处理
     * 1、申请领取成功 处理
     * 2、申请领取失败 处理
     * 3、返回
     */
    public <T,R> R apply(String redisMapKey, LimitWrapper<T> limitWrapper, Function<T,R> successApply, Function<T,R> failApply ){
        RMap<String, Long> limitMap = redissonClient.getMap(redisMapKey, LongCodec.INSTANCE);
        // 可以使用lua
        String limitKey = limitWrapper.limitType().limitKey(limitWrapper.markId());
        long applyCount = limitWrapper.applyCount();
        long limitCount = limitWrapper.limitCount();
        Long totalApply = limitMap.addAndGet( limitKey,applyCount );
        if( totalApply > limitCount){
            if( totalApply - applyCount < limitCount){
                limitMap.addAndGet( limitKey,- applyCount );
            }
            return failApply.apply( limitWrapper.params() );
        }
        return successApply.apply( limitWrapper.params() );
    }

}
