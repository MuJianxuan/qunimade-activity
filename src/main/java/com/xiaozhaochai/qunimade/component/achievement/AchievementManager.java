package com.xiaozhaochai.qunimade.component.achievement;

/**
 * 成就 管理人
 * @author Rao
 * @Date 2021/11/18
 **/
public interface AchievementManager {


    //====================constants=============
    /**
     * 目前成就值桶
     */
    String ACHIEVEMENT_VALUE_BUCKET_PREFIX = "achievement:value_bucket:";
    /**
     * 成就 阶段值桶  这个桶可以不用
     */
    @Deprecated
    String ACHIEVEMENT_NEXT_STAGE_VALUE_BUCKET_PREFIX = "achievement:stage_bucket:";
    /**
     *  所处阶段桶
     */
    String ACHIEVEMENT_IN_STAGE_BUCKET_PREFIX = "achievement:in_stage_bucket:";
    /**
     * 达成下一阶段得差值
     */
    String ACHIEVEMENT_REACH_NEXT_STAGE_DIFFERENCE_BUCKET_PREFIX = "achievement:reach_next_stage_bucket:";

    /**
     * 锁
     */
    String ACHIEVEMENT_LOCK_PREFIX = "achievement_lock:";

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
