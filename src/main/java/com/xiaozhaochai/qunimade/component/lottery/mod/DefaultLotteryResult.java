package com.xiaozhaochai.qunimade.component.lottery.mod;

import com.xiaozhaochai.qunimade.component.lottery.pool.LotteryPool;
import lombok.Data;

import java.util.List;

/**
 *
 * @author Rao
 * @Date 2021/12/01
 **/
@Data
public class DefaultLotteryResult implements LotteryResult {

    private static final long serialVersionUID = 6659958239310573998L;

    /**
     * 奖池信息
     */
    private LotteryPool lotteryPool;

    /**
     * 多抽 >> 抽中的奖励ID
     */
    private List<String> rewardIdList;


    /**
     * 多抽结果
     * @param lotteryPool
     * @param rewardIdList
     */
    public DefaultLotteryResult(LotteryPool lotteryPool, List<String> rewardIdList) {
        this.lotteryPool = lotteryPool;
        this.rewardIdList = rewardIdList;
    }

    @Override
    public LotteryPool lotteryPool() {
        return lotteryPool;
    }

    @Override
    public List<String> awardIdList() {
        return rewardIdList;
    }
}
