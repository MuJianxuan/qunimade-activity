package com.xiaozhaochai.qunimade.component.leaderboard.basic;

import com.xiaozhaochai.qunimade.component.leaderboard.comparator.LeaderboardLastUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户排名桶
 * @author Rao
 * @Date 2021/12/07
 **/
@Data
@Accessors(chain = true)
public class RankScoreEntry implements Serializable , LeaderboardLastUpdate {
    private static final long serialVersionUID = -4189869062405812695L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 排名
     */
    private int rank;

    /**
     * 分数值
     */
    private long score;

    /**
     * 最后更新时间
     */
    private long lastUpdateTime;

    public RankScoreEntry(String userId, int rank, long score) {
        this.userId = userId;
        this.rank = rank;
        this.score = score;
    }

    public RankScoreEntry(int rank, long score) {
        this.rank = rank;
        this.score = score;
    }

    @Override
    public long score() {
        return this.score;
    }

    @Override
    public long lastUpdateTimestamp() {
        return this.lastUpdateTime;
    }
}
