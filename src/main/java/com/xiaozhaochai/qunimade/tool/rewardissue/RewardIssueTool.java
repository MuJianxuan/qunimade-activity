package com.xiaozhaochai.qunimade.tool.rewardissue;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * 查找奖励发放
 * @author Rao
 * @Date 2021/12/09
 **/
@Slf4j
public class RewardIssueTool {

    /**
     * 寻找区间奖励
     * @return
     */
    public <T> Optional<RewardIssue<T>> findRewardIssue(List<RewardIssue<T>> rewardIssueList, long value ){
        // 第一个区间
        return rewardIssueList.stream().filter(rewardIssue -> rewardIssue.isThisReward( value)).findFirst();
    }


}
