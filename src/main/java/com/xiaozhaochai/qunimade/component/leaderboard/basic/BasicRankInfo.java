package com.xiaozhaochai.qunimade.component.leaderboard.basic;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 排名信息
 * @author Rao
 * @Date 2021/11/15
 **/
@Accessors(chain = true)
@Data
public class BasicRankInfo<T> implements Serializable {
    private static final long serialVersionUID = 4584572316782624665L;

    /**
     * 排名分数桶
     */
    private RankScoreEntry rankScoreEntry;

    /**
     * 用户信息
     */
    private BasicUserInfo userInfo;

    /**
     * 额外信息
     */
    private T extraData;

}
