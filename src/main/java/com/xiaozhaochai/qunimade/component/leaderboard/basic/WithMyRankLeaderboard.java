package com.xiaozhaochai.qunimade.component.leaderboard.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带有我的排名的排行榜信息
 * @author Rao
 * @Date 2021/11/15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class WithMyRankLeaderboard<T> extends AbstractLeaderboard<T> {
    private static final long serialVersionUID = 3292231884222707832L;

    /**
     * 我的排名信息
     */
    private BasicRankInfo<T> myRankInfo;

    /**
     * 非空
     */
    private Boolean notEmpty = true;

}
