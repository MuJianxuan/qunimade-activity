package com.xiaozhaochai.qunimade.component.lottery.mod;


import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;

import java.io.Serializable;
import java.util.List;

/**
 * @author Rao
 * @Date 2021/12/01
 **/
public interface LotteryResult extends Serializable {

    /**
     * 奖池
     * @return
     */
    LotteryPool lotteryPool();

    /**
     * 获取奖赏ID集合
     * @return
     */
    List<String> awardIdList();

}

