package com.xiaozhaochai.qunimade.component.leaderboard.basic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Rao
 * @Date 2021/11/15
 **/
@Data
public class BasicUserInfo implements Serializable {
    private static final long serialVersionUID = 8882978775326714531L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * en
     * @param userId
     * @param nickname
     * @param avatarUrl
     * @return
     */
    public BasicUserInfo ofUser(String userId, String nickname, String avatarUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        return this;
    }
}
