package com.xiaozhaochai.qunimade.component.leaderboard;



import com.xiaozhaochai.qunimade.component.leaderboard.basic.BasicRankInfo;
import com.xiaozhaochai.qunimade.component.leaderboard.basic.WithMyRankLeaderboard;

import java.util.List;

/**
 * @author Rao
 * @Date 2021/11/25
 **/
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
