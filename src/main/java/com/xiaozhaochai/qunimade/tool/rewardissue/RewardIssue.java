package com.xiaozhaochai.qunimade.tool.rewardissue;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 奖励发放 类
 * @author Rao
 * @Date 2021/11/23
 **/
@Data
@Accessors(chain = true)
public class RewardIssue<T> {

    private Integer start;

    private Integer end;

    /**
     * 无限大
     */
    private Boolean unlimited;

    /**
     * 无限小
     */
    private Boolean infinitelySmall;

    /**
     * value 应该重构
     */
    private T value;

    /**
     * 是这个奖励
     * @return
     */
    public boolean isThisReward(long num){

        if(unlimited != null && unlimited ){
            return start <= num;
        }

        if( infinitelySmall != null && infinitelySmall){
            return num <= end;
        }

        return start <= num && num <= end;
    }



}
