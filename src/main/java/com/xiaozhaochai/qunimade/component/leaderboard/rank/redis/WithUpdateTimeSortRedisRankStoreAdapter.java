package com.xiaozhaochai.qunimade.component.leaderboard.rank.redis;

import com.xiaozhaochai.qunimade.component.leaderboard.basic.RankScoreEntry;
import com.xiaozhaochai.qunimade.component.leaderboard.comparator.LeaderboardLastUpdateComparator;
import com.xiaozhaochai.qunimade.component.leaderboard.rank.RankStore;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 带更新时间的排名存储
 * @author 母鸡
 */
@Slf4j
public class WithUpdateTimeSortRedisRankStoreAdapter implements RankStore {

    /**
     * 更新时间的key
     */
    private static final String LAST_UPDATE_TIME_MAP_KEY = "_last_update_time_map";

    private final RankStore rankStore;
    private final RedissonClient redissonClient;

    public WithUpdateTimeSortRedisRankStoreAdapter(RankStore rankStore,
                                                   RedissonClient redissonClient) {
        this.rankStore = rankStore;
        this.redissonClient = redissonClient;
    }

    /**
     * 排行榜的key
     * @param rankKey
     * @return
     */
    private RMap<String, Long> leaderboardLastUpdateBucket(String rankKey){
        String rankUpdateTimeMapKey = rankKey + LAST_UPDATE_TIME_MAP_KEY;
        return redissonClient.getMap(rankUpdateTimeMapKey, JsonJacksonCodec.INSTANCE);
    }

    @Override
    public int refreshRank(String rankKey, String userId, long score) {
        this.leaderboardLastUpdateBucket( rankKey).fastPut( userId,System.currentTimeMillis() );
        return rankStore.refreshRank( rankKey,userId,score);
    }

    @Override
    public RankScoreEntry getRankScoreEntry(String rankKey, String userId) {
        return rankStore.getRankScoreEntry( rankKey,userId);
    }

    @Override
    public List<RankScoreEntry> page(String rankKey, int page, int size) {
        List<RankScoreEntry> rankScoreEntryList = rankStore.page(rankKey, page, size);
        Map<String, Long> userIdLastUpdateTimeMap = this.leaderboardLastUpdateBucket(rankKey).getAll(rankScoreEntryList.stream().map(RankScoreEntry::getUserId).collect(Collectors.toSet()));
        rankScoreEntryList.forEach(rankScoreEntry -> rankScoreEntry.setLastUpdateTime( userIdLastUpdateTimeMap.getOrDefault(rankScoreEntry.getUserId(),0L )));
        rankScoreEntryList.sort( new LeaderboardLastUpdateComparator() );
        int rank = 1;
        for (RankScoreEntry rankScoreEntry : rankScoreEntryList) {
            rankScoreEntry.setRank( rank++);
        }
        return rankScoreEntryList;
    }

    @Override
    public List<String> listRangeUserIds(String rankKey, int page, int size) {
        return rankStore.listRangeUserIds(rankKey,page,size);
    }
}
