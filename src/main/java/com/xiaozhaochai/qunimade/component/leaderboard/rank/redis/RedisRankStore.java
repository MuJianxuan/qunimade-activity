package com.xiaozhaochai.qunimade.component.leaderboard.rank.redis;

import com.xiaozhaochai.qunimade.component.leaderboard.basic.RankScoreEntry;
import com.xiaozhaochai.qunimade.component.leaderboard.rank.RankStore;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.ScoredEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Rao
 * @Date 2021/12/07
 **/
public class RedisRankStore implements RankStore {

    private final RedissonClient redissonClient;

    public RedisRankStore(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 排序池
     * @param rankKey
     * @return
     */
    private RScoredSortedSet<String> scoredSortedSet(String rankKey){
        return redissonClient.getScoredSortedSet(rankKey, StringCodec.INSTANCE);
    }

    @Override
    public int refreshRank(String rankKey, String userId, long score) {
        return Optional.ofNullable( this.scoredSortedSet( rankKey).addScoreAndGetRevRank(userId, score) ).orElse( -1 ) ;
    }

    @Override
    public RankScoreEntry getRankScoreEntry(String rankKey, String userId) {
        RBatch batch = redissonClient.createBatch();
        batch.getScoredSortedSet(rankKey, StringCodec.INSTANCE).revRankAsync(userId);
        batch.getScoredSortedSet(rankKey, StringCodec.INSTANCE).getScoreAsync(userId);
        List<?> responseResult = batch.execute().getResponses();
        int rank = Optional.ofNullable( (Integer) responseResult.get(0) ).orElse( -1) + 1;
        long score = Optional.ofNullable( (Double) responseResult.get(1) ).orElse( 0.0).longValue();
        return new RankScoreEntry( userId, rank, score);
    }

    @Override
    public List<RankScoreEntry> page(String rankKey, int page, int size) {
        page = Math.max(1, page);
        Collection<ScoredEntry<String>> scoredEntries = this.scoredSortedSet(rankKey).entryRangeReversed(page, size);
        int rank = 1;
        List<RankScoreEntry> rankScoreEntryList = new ArrayList<>(size+1);
        for (ScoredEntry<String> userIdScoreEntry : scoredEntries) {
            rankScoreEntryList.add( new RankScoreEntry(userIdScoreEntry.getValue(),rank++, Optional.ofNullable( userIdScoreEntry.getScore() ).orElse(0.0).longValue() ) );
        }
        return rankScoreEntryList;
    }

    @Override
    public List<String> listRangeUserIds(String rankKey, int page, int size ) {
        page = Math.max(1, page);
        return new ArrayList<>(this.scoredSortedSet(rankKey).valueRangeReversed(page, size));
    }
}
