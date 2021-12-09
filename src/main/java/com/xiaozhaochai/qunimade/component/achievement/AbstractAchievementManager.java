package com.xiaozhaochai.qunimade.component.achievement;

import cn.hutool.core.lang.Assert;

import com.xiaozhaochai.qunimade.component.achievement.stage.AchievementStage;
import com.xiaozhaochai.qunimade.component.globallock.DistributedLock;
import com.xiaozhaochai.qunimade.component.globallock.redisson.RedissonDistributedLock;
import com.xiaozhaochai.qunimade.component.globallock.lock.LockWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.InitializingBean;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Rao
 * @Date 2021/11/18
 **/
@Slf4j
public abstract class AbstractAchievementManager implements AchievementManager, InitializingBean {

    /**
     * 阶段对象  初始化所处阶段值是 0
     */
    private AchievementStage achievementStage;

    private final RedissonClient redissonClient;

    private final DistributedLock distributedLock;

    public AbstractAchievementManager(RedissonClient redissonClient, DistributedLock distributedLock) {
        this.redissonClient = redissonClient;
        this.distributedLock = distributedLock;
    }


    protected AchievementStage getAchievementStage() {
        return achievementStage;
    }

    /**
     * 获取成就业务名称
     * @return
     */
    protected abstract String getAchievementServiceName();

    /**
     * 目前成就值桶
     * @return
     */
    protected String buildAchievementValueBucketKey(){
        return AchievementManager.ACHIEVEMENT_VALUE_BUCKET_PREFIX + this.buildKey();
    }

    /**
     * 下一成就阶段值桶key
     * @return
     */
    @Deprecated
    protected String buildAchievementNextStageValueBucketKey(){
        return AchievementManager.ACHIEVEMENT_NEXT_STAGE_VALUE_BUCKET_PREFIX + this.buildKey();
    }

    /**
     * 所处阶段桶
     * @return
     */
    protected String buildAchievementCurrentInStageBucketKey(){
        return AchievementManager.ACHIEVEMENT_IN_STAGE_BUCKET_PREFIX + this.buildKey();
    }

    /**
     * 达成下一阶段得差值
     * @return
     */
    protected String buildAchievementReachNextStageDifferenceBucketKey(){
        return AchievementManager.ACHIEVEMENT_REACH_NEXT_STAGE_DIFFERENCE_BUCKET_PREFIX + this.buildKey();
    }

    /**
     * 组装key
     */
    protected String buildKey(){
        String achievementServiceName = this.getAchievementServiceName();
        Assert.notNull( achievementServiceName);
        return achievementServiceName;
    }

    /**
     * 添加成就值
     * @param userId
     * @param achievementValue
     */
    @Override
    public void addAchievementValue(String userId, Long achievementValue) {

        // 当前用户成就值桶
        String key = AchievementManager.ACHIEVEMENT_LOCK_PREFIX + this.getAchievementServiceName() + ":" + userId;
        LockWrapper lockWrapper = new LockWrapper().setKey(key).setWaitTime(5).setLeaseTime(5).setUnit(TimeUnit.SECONDS);

        distributedLock.tryLock( lockWrapper,
                () -> {
                    RMap<String, Long> achievementValueBucket = redissonClient.getMap( this.buildAchievementValueBucketKey(), LongCodec.INSTANCE);
                    //  ? 新增成就值后比较  没值和有值的情况：
                    boolean firstAddAchievement = this.isFirstAddAchievement(achievementValueBucket, userId);
                    // 添加当前用户的 成就值
                    achievementValueBucket.addAndGet( userId, achievementValue);
                    this.coreProcess( userId,achievementValue, firstAddAchievement);
                    return null;
                },
                () -> {
                    log.error("[addAchievementValue] {} lock fail! userId:{},achievementValue:{} ",this.getAchievementServiceName(),userId,achievementValue);
                    return null;
                }
        );

    }

    /**
     * 是否是 第一次添加 成就值
     * @return
     */
    protected boolean isFirstAddAchievement(RMap<String, Long> achievementValueBucket,String userId){
        // dd 做缓存减少 网络调用
        // 不存在说明是第一次
        return ! achievementValueBucket.containsKey(userId);

    }

    /**
     * 核心处理
     *   需要改变 1、下一阶段成就达成值桶 2、所处阶段值桶
     * @param userId
     * @param achievementValue 新增的值
     * @param firstAddAchievement 第一次添加成就值
     */
    protected void coreProcess(String userId, Long achievementValue, boolean firstAddAchievement){
        // 当前所处阶段桶
        RMap<String, Integer> achievementCurrentInStageBucket = redissonClient.getMap( this.buildAchievementCurrentInStageBucketKey(), IntegerCodec.INSTANCE);
        // 达到下一阶段差值桶
        RMap<String, Long> achievementReachNextStageDifferenceBucket = redissonClient.getMap(this.buildAchievementReachNextStageDifferenceBucketKey(), LongCodec.INSTANCE);

        // 是否初始化
        if( firstAddAchievement){
            // 当前阶段
            achievementCurrentInStageBucket.put( userId, 0 );
            // 下一阶段成就值
            Long nextAchievementStageValue = this.achievementStage.nextAchievementStageValue(0);
            // 差值 即是 初始值
            achievementReachNextStageDifferenceBucket.put( userId, nextAchievementStageValue);
        }

        // 达成下一阶段的差值
        Long achievementReachNextStageValue = Optional.ofNullable( achievementReachNextStageDifferenceBucket.get(userId) ).orElse(-1L);
        // 如果成就任务已经完成了
        if( achievementReachNextStageValue < 0 ){
            this.finishedAllStage( userId );
            return;
        }

        this.recursiveCalculationReachAchievement(userId,achievementValue,achievementReachNextStageValue,
                achievementCurrentInStageBucket,
                achievementReachNextStageDifferenceBucket
        );



    }

    /**
     * 完成了所以成就任务  阶段完成后一直会触发
     * @param userId
     */
    protected void finishedAllStage(String userId){
        if (log.isDebugEnabled()) {
            log.debug("[tactivity >> {}:user:{} finishedAllStage! ",userId,this.getAchievementServiceName());
        }
        // give son handle
    };

    /**
     * 递归计算达成成就
     * @param achievementValue 新增的值
     * @param achievementReachNextStageValue 达成下一阶段成就的差值
     */
    protected void recursiveCalculationReachAchievement(String userId ,Long achievementValue ,Long achievementReachNextStageValue,
                                                        RMap<String, Integer> achievementCurrentInStageBucket,
                                                        RMap<String, Long> achievementReachNextStageDifferenceBucket
                                                        ){

        // 当前阶段
        Integer currentInStage = achievementCurrentInStageBucket.get(userId);

        // 达成目标的情况 >> 递归
        if( achievementValue >= achievementReachNextStageValue ){

            // 剩余值
            long remaining = achievementValue - achievementReachNextStageValue;
            // 桶添加阶段
            int nextInState = currentInStage + 1;
            achievementCurrentInStageBucket.put(userId, nextInState);

            // 新的阶段的要达成的差值  下一阶段减去当前阶段的值  if  负数 则表示 没有后续阶段了
            Long nextAchievementStageValue = Optional.ofNullable( this.achievementStage.nextAchievementStageValue(nextInState) ).orElse(0L) - Optional.ofNullable(this.achievementStage.nextAchievementStageValue( currentInStage)).orElse(1L);

            // 添加下一阶段差值
            achievementReachNextStageDifferenceBucket.put( userId, nextAchievementStageValue);

            try {
                // 同时触发一次
                achievementTrigger( userId, this.achievementStage.nextAchievementStageValue( currentInStage));
            }catch (Exception ex){
                log.error("[tactivity >> {}, achievementTrigger error!]  ",this.getAchievementServiceName(),ex);
            }

            // 减少入栈
            if( remaining == 0 || nextAchievementStageValue < 0){
                return;
            }

            this.recursiveCalculationReachAchievement(userId,remaining,nextAchievementStageValue,
                    achievementCurrentInStageBucket,
                    achievementReachNextStageDifferenceBucket
            );

        }
        // 改变差值
        else{
            // 添加差值
            achievementReachNextStageDifferenceBucket.addAndGet( userId,-achievementValue);

        }

    }


    /**
     * 达成成就触发
     * @param userId 用户Id
     * @param achievementValue 达成成就值
     */
    public abstract void achievementTrigger(String userId,Long achievementValue);

    @Override
    public long getAchievementValue(String userId) {
        RMap<String, Long> achievementValueBucket = redissonClient.getMap( this.buildAchievementValueBucketKey(), LongCodec.INSTANCE);
        // 一开始没得
        return Optional.ofNullable( achievementValueBucket.get( userId)).orElse(0L);
    }

    @Override
    public long getNextStageAchievementValue(String userId) {
        RMap<String, Integer> achievementCurrentInStageBucket = redissonClient.getMap( this.buildAchievementCurrentInStageBucketKey(), IntegerCodec.INSTANCE);
        return this.achievementStage.nextAchievementStageValue( achievementCurrentInStageBucket.getOrDefault(userId,0));
    }

    @Override
    public long getBeInStage(String userId) {
        return 0;
    }


    public void setAchievementStage(AchievementStage achievementStage) {
        Assert.notNull( achievementStage);
        this.achievementStage = achievementStage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setAchievementStage( this.initAchievementStage());
    }

    /**
     * 初始化 阶段对象
     * @return
     */
    protected abstract AchievementStage initAchievementStage();

}
