package com.xiaozhaochai.qunimade.tool.limited.mod;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rao
 * @Date 2021/12/03
 **/
@Data
@Slf4j
@Accessors(chain = true)
public class DefaultLimit<T> implements LimitWrapper<T> {

    /**
     * ID
     * @return
     */
    private String markId;

    /**
     * 申请数
     * @return
     */
    private long applyCount;

    /**
     * 限制
     */
    private long limitCount;

    /**
     * 参数
     * @return
     */
    private T params;

    /**
     * 限量类型
     */
    private LimitedTypeEnum limitType;

    @Override
    public String markId() {
        return markId;
    }

    @Override
    public long applyCount() {
        return applyCount;
    }

    @Override
    public long limitCount() {
        return 0;
    }

    @Override
    public T params() {
        return params;
    }

    @Override
    public LimitedTypeEnum limitType() {
        return limitType;
    }
}
