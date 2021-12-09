package com.xiaozhaochai.qunimade.component.leaderboard.rank;

import com.xiaozhaochai.qunimade.component.leaderboard.basic.RankScoreEntry;

import java.util.List;

/**
 * 排名持久化
 * @author Rao
 * @Date 2021/12/07
 **/
public interface RankStore {

    /**
     * 刷新排名
     * @param rankKey
     * @param userId
     * @param score
     * @return rank
     */
    int refreshRank(String rankKey,String userId,long score);


    /**
     * 获取排名与分数
     * @param rankKey
     * @param userId
     * @return
     */
    RankScoreEntry getRankScoreEntry(String rankKey, String userId);

    /**
     * 分页
     * @param rankKey
     * @param page 从 1 开始
     * @param size
     */
    List<RankScoreEntry> page(String rankKey, int page, int size);

    /**
     * 获取 范围内的 用户ID
     * @param rankKey
     * @param page 1
     * @param size
     * @return
     */
    List<String> listRangeUserIds(String rankKey,int page, int size);

}
