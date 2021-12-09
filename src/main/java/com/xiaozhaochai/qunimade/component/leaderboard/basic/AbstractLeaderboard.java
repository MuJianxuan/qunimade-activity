package com.xiaozhaochai.qunimade.component.leaderboard.basic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 含有个人信息的排行榜单
 * @author Rao
 * @Date 2021/11/15
 **/
@Data
public abstract class AbstractLeaderboard<T> implements Serializable {
    private static final long serialVersionUID = 7811725258389593831L;

    /**
     * 榜单列表
     */
    private List<? extends BasicRankInfo<T>> rankInfoList;

}
