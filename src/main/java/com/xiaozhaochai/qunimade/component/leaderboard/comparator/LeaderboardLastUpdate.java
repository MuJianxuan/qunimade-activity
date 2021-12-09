package com.xiaozhaochai.qunimade.component.leaderboard.comparator;

/**
 * @author Rao
 * @Date 2021/11/23
 **/
public interface LeaderboardLastUpdate {

    /**
     * 分数
     * @return
     */
    long score();

    /**
     * 最后更新的时间蹉
     * @return
     */
    long lastUpdateTimestamp();

}
