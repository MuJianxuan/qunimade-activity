package com.xiaozhaochai.qunimade.component.lottery;

import cn.hutool.core.lang.Assert;
import com.xiaozhaochai.qunimade.component.globallock.DistributedLock;
import com.xiaozhaochai.qunimade.component.globallock.lock.LockWrapper;
import com.xiaozhaochai.qunimade.component.lottery.exe.LotteryFailException;
import com.xiaozhaochai.qunimade.component.lottery.lotterystrategy.LotteryStrategy;
import com.xiaozhaochai.qunimade.component.lottery.mod.DefaultLotteryResult;
import com.xiaozhaochai.qunimade.component.lottery.mod.LotteryParams;
import com.xiaozhaochai.qunimade.component.lottery.mod.LotteryResult;
import com.xiaozhaochai.qunimade.component.lottery.pool.DefaultLotteryPool;
import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;
import com.xiaozhaochai.qunimade.component.lottery.reward.LotteryReward;
import com.xiaozhaochai.qunimade.component.lottery.storage.LotteryStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 任何参数都不可能是 基本类型 而是一个 多态的，但如何定义呢？
 * P >> params , R >>  result
 *  重构：
 *  1、池子锁
 *  2、用户锁
 * @author Rao
 * @Date 2021/11/16
 **/
@Slf4j
public abstract class AbstractLotteryAssistant<R,P extends LotteryParams> implements LotteryAssistant<R,P> {

    /**
     * 抽奖存储
     */
    private final LotteryStorage lotteryStorage;
    /**
     * 抽奖策略
     */
    private final LotteryStrategy lotteryStrategy;

    private final DistributedLock distributedLock;

    public AbstractLotteryAssistant(LotteryStorage lotteryStorage, LotteryStrategy lotteryStrategy, DistributedLock distributedLock) {
        this.lotteryStorage = lotteryStorage;
        this.lotteryStrategy = lotteryStrategy;
        this.distributedLock = distributedLock;
    }

    /**
     * 抽奖失败
     */
    final static Supplier<DefaultLotteryResult> LOTTERY_FAIL_SUPPLIER = () -> {throw new LotteryFailException();};


    /**
     * 移除式抽奖
     */
    @Override
    public R removableSweepStake(P params) {
        try {
            String lotteryPoolId = params.lotteryPoolId();
            int lotteryCount = params.lotteryCount();
            String key = this.buildLotteryPoolKey(lotteryPoolId);

            // 检查奖池
            lotteryStorage.checkLotteryNotEmpty( key,lotteryCount );

            // 获取全局锁的key
            String lotteryGlobalLockKey = this.buildLockLotteryGlobalKey( key);
            LockWrapper lockWrapper = new LockWrapper().setKey( lotteryGlobalLockKey ).setWaitTime(3).setLeaseTime( lotteryCount * 5).setUnit(TimeUnit.SECONDS);

            LotteryResult lotteryResult = distributedLock.tryLock(
                    lockWrapper,
                    () -> {
                        // 抽奖前校验奖池
                        LotteryPool lotteryPool = lotteryStorage.getWithCheckByKeyAndId(key, lotteryPoolId, lotteryCount);
                        // 策略抽奖
                        List<String> awardIdList = lotteryStrategy.removableSweepStake(lotteryPool, lotteryCount);
                        // 对抽奖结果进行处理
                        this.removableStakeLockProcess( params, awardIdList);
                        // 返回抽奖结果
                        return new DefaultLotteryResult(lotteryPool, awardIdList);
                    },
                    LOTTERY_FAIL_SUPPLIER
            );
            // 后置操作 >> ?记录 >> ?返回结果
            return this.successLottery( params,lotteryResult );

        }catch (Exception ex){
            //抽奖失败
            if (log.isDebugEnabled()) { log.debug("[AbstractLotteryAssistant] lineUpSweepStake: {} ，lottery fail!",this.getLotteryName(),ex); }
            return this.failLottery(params);
        }

    }

    /**
     * 非移除式抽奖
     * @param params
     * @return
     */
    @Override
    public R nonRemovableSweepStake(P params) {

        try {
            String lotteryPoolId = params.lotteryPoolId();
            int lotteryCount = params.lotteryCount();
            String key = this.buildLotteryPoolKey(lotteryPoolId);

            // 获取全局锁的key
            String lotteryGlobalLockKey = this.buildLockLotteryGlobalKey(  params.userId() );
            LockWrapper lockWrapper = new LockWrapper().setKey( lotteryGlobalLockKey ).setWaitTime(3).setLeaseTime( 5).setUnit(TimeUnit.SECONDS);

            // 获取奖池
            LotteryPool lotteryPool = lotteryStorage.getWithCheckByKeyAndId(key, lotteryPoolId, lotteryCount);

            // 获取用户锁
            DefaultLotteryResult lotteryResult = distributedLock.tryLock( lockWrapper, () -> {
                List<String> awardIdList = lotteryStrategy.nonRemovableSweepStake(lotteryPool, lotteryCount);
                this.lockProcess(params, awardIdList);
                return new DefaultLotteryResult(lotteryPool, awardIdList);
            }, LOTTERY_FAIL_SUPPLIER);

            return this.successLottery(params,lotteryResult );
        } catch (Exception ex) {
            //抽奖失败
            if (log.isDebugEnabled()) {
                log.debug("[AbstractLotteryAssistant] nonLineUpSweepStake: {} ，lottery fail!", this.getLotteryName(), ex);
            }
            return this.failLottery(params);
        }
    }

    /**
     * 排队式抽奖 核心处理
     * @param params
     * @param awardIdList 奖品Id集合
     */
    private void removableStakeLockProcess(P params, List<String> awardIdList){
        // 扩展
        if ( this.lockProcess( params, awardIdList)) {
            // 移除奖品
            lotteryStorage.batchRemoveRewardByIds( this.buildLotteryPoolKey( params.lotteryPoolId() ),awardIdList);
        }
        // lottery fail
        throw new LotteryFailException();
    }

    /**
     * false is lottery fail
     * @param params
     * @param awardIdList
     */
    protected boolean lockProcess(P params, List<String> awardIdList){
        // give son handle ....
        // eg: 扣减用户的 抽奖值
        return true;
    }

    /**
     * 抽奖成功
     * @param lotteryResult  抽奖结果
     * @return
     */
    protected abstract R successLottery(P params,LotteryResult lotteryResult);

    /**
     * 抽奖失败
     * @return
     */
    protected abstract R failLottery(P params);

    /**
     * 获取奖池名称
     *   eg:
     *      blackFridayLottery
     *      liveRoomLottery
     *      ...
     * @return
     */
    protected abstract String getLotteryName();

    /**
     * 不通业务的Id可能相同
     * @param lotteryPoolId
     * @return
     */
    private String buildLotteryPoolKey(String lotteryPoolId){
        return this.getLotteryName()+":"+lotteryPoolId;
    }

    /**
     * 解析Id
     */
    private String parseLotteryPoolId(String lotteryPoolKey){
        return lotteryPoolKey.split(":")[1];
    }

    /**
     *  触发场景：成就达成后触发会生成宝箱
     * 初始化池子
     */
    @Override
    public boolean initLotteryPool(String lotteryPoolId,  List<? extends LotteryReward> lotteryRewardList) {
        // 带业务名称的 奖池key
        String lotteryPoolKey = this.buildLotteryPoolKey(lotteryPoolId);

        LotteryPool lotteryPool = new DefaultLotteryPool( lotteryPoolKey, lotteryPoolId);
        lotteryPool.addLottery( lotteryRewardList);

        // 交给持久化
        Assert.isTrue( lotteryStorage.clear( lotteryPool) );
        lotteryStorage.storageLottery( lotteryPool);
        return  true;

    }
}
