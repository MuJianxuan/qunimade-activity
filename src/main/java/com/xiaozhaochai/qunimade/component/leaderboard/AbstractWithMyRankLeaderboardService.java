package com.xiaozhaochai.qunimade.component.leaderboard;


import com.xiaozhaochai.qunimade.component.leaderboard.basic.BasicRankInfo;
import com.xiaozhaochai.qunimade.component.leaderboard.basic.BasicUserInfo;
import com.xiaozhaochai.qunimade.component.leaderboard.basic.RankScoreEntry;
import com.xiaozhaochai.qunimade.component.leaderboard.basic.WithMyRankLeaderboard;
import com.xiaozhaochai.qunimade.component.leaderboard.rank.RankStore;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 让组件彻底成组件
 * @author Rao
 * @Date 2021/11/24
 **/
public abstract class AbstractWithMyRankLeaderboardService<T> implements LeaderboardService<T> {

    private final RankStore rankStore;

    public AbstractWithMyRankLeaderboardService(RankStore rankStore) {
        this.rankStore = rankStore;
    }

    /**
     * 排行榜key
     * @return
     */
    protected abstract String leaderboardKey();

    /**
     * 添加分数
     * @param userId
     * @param score
     * @return
     */
    @Override
    public int refreshRank(String userId, long score){
        return rankStore.refreshRank(leaderboardKey(), userId, score);
    }

    /**
     * @param pageNo 从1 开始
     * @param pageSize
     * @return
     */
    @Override
    public WithMyRankLeaderboard<T> getWithMyRankLeaderboard(int pageNo, int pageSize){

        WithMyRankLeaderboard<T> withMyRankLeaderboard = new WithMyRankLeaderboard<>();
        List<BasicRankInfo<T>> leaderboardData = this.getLeaderboardData(pageNo, pageSize);

        // 处理我的信息
        String currentLoginUserId = this.getCurrentLoginUserId();
        Optional<BasicRankInfo<T>> myRankInfoOpt = leaderboardData.stream().filter(rankInfo -> this.getCurrentLoginUserId().equals(rankInfo.getUserInfo().getUserId())).findFirst();
        if (myRankInfoOpt.isPresent()) {
            withMyRankLeaderboard.setMyRankInfo( myRankInfoOpt.get() );
        }
        // 再查询
        else{
            BasicRankInfo<T> myRankInfo = this.getUserRankInfo(currentLoginUserId);
            withMyRankLeaderboard.setMyRankInfo( myRankInfo );
        }
        withMyRankLeaderboard.setRankInfoList( leaderboardData);

        return withMyRankLeaderboard;
    }

    /**
     * 查询 榜单数据
     * @param pageNo 1
     * @param pageSize
     * @return
     */
    @Override
    public List<BasicRankInfo<T>> getLeaderboardData(int pageNo, int pageSize) {
        List<RankScoreEntry> rankScoreEntryList = rankStore.page(leaderboardKey(), pageNo, pageSize);
        if(CollectionUtils.isEmpty( rankScoreEntryList)) {
            return new ArrayList<>();
        }
        List<String> userIdList = rankScoreEntryList.stream().map( RankScoreEntry::getUserId ).collect( Collectors.toList());
        List<BasicUserInfo> userInfoList = this.listByIds(userIdList);
        Map<String,BasicUserInfo> idUserMap = userInfoList.stream().collect( Collectors.toMap( BasicUserInfo::getUserId, Function.identity()));

        return rankScoreEntryList.stream().map(rankScoreEntry -> {
            String userId = rankScoreEntry.getUserId();
            BasicRankInfo<T> rankInfo = new BasicRankInfo<>();
            BasicUserInfo userInfo = idUserMap.getOrDefault( userId, new BasicUserInfo());
            rankInfo.setRankScoreEntry( rankScoreEntry);
            rankInfo.setUserInfo( userInfo);
            return rankInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取当前登录的 用户Id
     * @return
     */
    protected abstract String getCurrentLoginUserId();

    /**
     * 查询用户信息
     * @param userIds
     * @return
     */
    protected abstract List<BasicUserInfo> listByIds(Collection<String> userIds);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public BasicRankInfo<T> getUserRankInfo(String userId){

        BasicUserInfo userInfo = this.getUserInfo(userId);
        RankScoreEntry rankScoreEntry = rankStore.getRankScoreEntry(this.leaderboardKey(), userId);
        return buildBasicRankInfo( userInfo, rankScoreEntry);
    }

    /**
     * 封装构建对象方法
     * @param userInfo
     * @param rankScoreEntry
     * @return
     */
    private BasicRankInfo<T> buildBasicRankInfo(BasicUserInfo userInfo, RankScoreEntry rankScoreEntry) {
        BasicRankInfo<T> basicRankInfo = new BasicRankInfo<>();
        basicRankInfo.setUserInfo( userInfo );
        basicRankInfo.setRankScoreEntry( rankScoreEntry);
        return basicRankInfo;
    }

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    protected abstract BasicUserInfo getUserInfo(String userId);

}
