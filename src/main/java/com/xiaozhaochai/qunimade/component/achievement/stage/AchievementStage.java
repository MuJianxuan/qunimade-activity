package com.xiaozhaochai.qunimade.component.achievement.stage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成就阶段
 * @author Rao
 * @Date 2021/11/18
 **/
@Slf4j
@Data
public class AchievementStage {

    /**
     * 固定key
     */
    private final static String FIXED_KEY = "fixed";

    /**
     * 第一阶段值
     */
    private final static String FIRST_STAGE_KEY = "first_stage";

    /**
     * 固定值 : xN
     *
     * 非固定值  10,30,50
     *     first_stage -> 10  初始阶段
     *     1     ->    30    第一阶段
     *     2    ->    50    第二阶段
     */
    private final Map<String,Long> achievementStageMap = new HashMap<>();
    /**
     * 是否等值
     */
    private final Boolean equivalent;


    /**
     * 固值
     * @param equivalentValue 每一阶段值
     */
    public AchievementStage(Long equivalentValue) {
        this.equivalent = true;
        Assert.notNull( equivalentValue);
        this.achievementStageMap.put( FIRST_STAGE_KEY, equivalentValue);
        this.achievementStageMap.put( FIXED_KEY, equivalentValue);
    }

    /**
     * 非固值
     * @param achievementStageValueList 固定成就阶段值列表
     */
    public AchievementStage(List<Long> achievementStageValueList ){
        Assert.isTrue(CollUtil.isNotEmpty( achievementStageValueList));
        this.equivalent = false;
        // 从小到大排列  o1 >= o2  为 1 表顺序  o1 >= o1 表倒序
        achievementStageValueList.sort( (o1,o2) -> o1 >= o2 ? 0 : -1);

        // 第一阶段值 20000L, 50000L, 100000L, 200000L
        this.achievementStageMap.put( FIRST_STAGE_KEY,achievementStageValueList.get( 0));
        for (int i = 1; i < achievementStageValueList.size(); i++) {
            // 阶段设置  10,30,50
            // f --> 10
            // 1 --> 30
            // 2 --> 50
            this.achievementStageMap.put( i+"" ,achievementStageValueList.get(i));
        }
    }

    /**
     * 获取下一阶段成就值
     * @param currentInStageValue  当前所处阶段值
     * @return  is null，表示没有下一阶段了
     */
    public Long nextAchievementStageValue(Integer currentInStageValue){
        if(  currentInStageValue == 0 ){
            return this.achievementStageMap.get( FIRST_STAGE_KEY);
        }

        if( equivalent ){
            return currentInStageValue * this.achievementStageMap.get( FIXED_KEY);
        }

        return this.achievementStageMap.get( currentInStageValue+"");
    }
}
