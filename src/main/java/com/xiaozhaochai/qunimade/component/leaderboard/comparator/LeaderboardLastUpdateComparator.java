package com.xiaozhaochai.qunimade.component.leaderboard.comparator;

import java.util.Comparator;

/**
 * 比较器
 * @author Rao
 * @Date 2021/11/23
 **/
public class LeaderboardLastUpdateComparator implements Comparator<LeaderboardLastUpdate> {
    /**
     *  简单用交换排序算法去理解即可
     * @param o1
     * @param o2
     * @return -1 交换位置
     */
    @Override
    public int compare(LeaderboardLastUpdate o1, LeaderboardLastUpdate o2) {

        if( o1.score() == o2.score()){
            if( o1.lastUpdateTimestamp() == o2.lastUpdateTimestamp() ){
                // 不交换位置
                return 0;
            }
            return o1.lastUpdateTimestamp() < o2.lastUpdateTimestamp() ? -1 : 1;
        }
        return 0;

    }
}
