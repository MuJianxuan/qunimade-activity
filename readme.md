# qunimade-activity
> 业务组件：但愿我们写业务能像写一个框架一样，有结构有层次的设计。模板方法流程设计，对通用的业务做抽象的封装，从而达到复用的目的。


## 分布式锁（DistributedLock）

目前实现有  RedissonDistributedLock ；

业务描述：分布式锁，全局资源控制，

使用：

```java
public interface DistributedLock {

    /**
     * tryLock
     * @param lockWrapper
     * @param successLockSupplier
     * @param failLockSupplier
     * @param <T>
     * @return
     */
    <T> T tryLock(LockWrapper lockWrapper, Supplier<T> successLockSupplier, Supplier<T> failLockSupplier);

}

```

## 排行榜（LeaderboardService）

业务描述：排行榜列表，带有我的信息展示的排行榜，当业务中有需要用到排行榜时，可以考虑使用，如果排行榜需要展示 额外的数据，可以重载父类方法，实现排行榜展示数据，能满足大部分应用场景。

RankStore  排名存储

- 带更新时间排序的  WithUpdateTimeSortRedisRankStoreAdapter
- 默认Redis SortSet  实现的 RedisRankStore

使用：

自定义业务类 继承 抽象的  AbstractWithMyRankLeaderboardService  类 即可。

```java
public interface LeaderboardService<T> {

    /**
     * 添加分数
     * @param userId
     * @param score
     * @return
     */
    int refreshRank(String userId, long score);

    /**
     * 查询带我的信息的榜单
     * @param pageNo 从1 开始
     * @param pageSize
     * @return
     */
    WithMyRankLeaderboard<T> getWithMyRankLeaderboard(int pageNo, int pageSize);

    /**
     * 查询 榜单数据
     * @param pageNo 1
     * @param pageSize
     * @return
     */
    List<BasicRankInfo<T>> getLeaderboardData(int pageNo, int pageSize);

}
```

## 限量 （LimitedService）

业务描述：有限的申领服务，当业务中使用到某个奖品限量时，我们可以采用该类去实现，但需要自定义key，限量换种想法其实就是 申请领取份数，申请不到则表示领取失败。可以用于每日限额领取。

```java
java@Slf4j
public class RedissonLimitedService {

    private final RedissonClient redissonClient;

    public RedissonLimitedService(RedissonClient redissonClient) {
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
```

## 抽奖助手 （LotteryAssistant）

业务描述：转盘抽奖 、宝箱抽奖等场景，结合限量服务可以实现 抽奖后的奖励进行 限量处理。

LotteryAssistant  提供了两个方法入口和初始化

```java
public interface LotteryAssistant<R,P extends LotteryParams> {

  ...
  
    /**
     * 排队式抽奖方法, 意味着会移除奖池的奖励
     *   with lock
     */
    R removableSweepStake(P params);

    /**
     * 非排队式抽奖方法，意味着不会移除奖池的奖励
     *   no lock
     */
    R nonRemovableSweepStake(P params);

    /**
     *  初始化奖池
     */
    boolean initLotteryPool(String lotteryPoolId, List<? extends LotteryReward> lotteryRewardList);

}
```

initLotteryPool : 初始化奖池，参数 奖池ID与礼物信息。

## 成就助手（AchievementManager）

业务描述：比如用户收礼数达到某个值，触发任务完成，领取某个奖励。

```java
public interface AchievementManager {


    //====================constants=============
  	...
    //====================constants=============


    /**
     * 增加成就值
     */
    void addAchievementValue(String userId, Long achievementValue);

    /**
     * 获取成就值
     * @param userId
     * @return
     */
    long getAchievementValue(String userId);

    /**
     * 获取下一阶段目标值
     */
    long getNextStageAchievementValue(String userId);

    /**
     * 当前用户是 第几阶段
     */
    long getBeInStage(String userId);

}
```

## 奖励限制

业务描述：比如用户积分在某个范围可以兑换到金币价值。











