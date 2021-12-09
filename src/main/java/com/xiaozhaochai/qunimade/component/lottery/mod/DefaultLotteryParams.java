package com.xiaozhaochai.qunimade.component.lottery.mod;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Rao
 * @Date 2021/11/28
 **/
@Data
@Accessors(chain = true)
public class DefaultLotteryParams implements Serializable , LotteryParams {
    private static final long serialVersionUID = 3517358905609377947L;

    private String lotteryId;

    private Integer count;

    private String userId;

    public DefaultLotteryParams() {
    }

    public DefaultLotteryParams(String lotteryId, Integer count, String userId) {
        this.lotteryId = lotteryId;
        this.count = count;
        this.userId = userId;
    }

    @Override
    public String lotteryPoolId() {
        return lotteryId;
    }

    @Override
    public int lotteryCount() {
        return Optional.ofNullable( count ).orElseThrow(() -> new RuntimeException(" count params not exist!"));
    }

    @Override
    public String userId() {
        return userId;
    }
}
