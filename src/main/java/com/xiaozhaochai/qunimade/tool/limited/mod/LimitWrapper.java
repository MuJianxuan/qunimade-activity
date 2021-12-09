package com.xiaozhaochai.qunimade.tool.limited.mod;

/**
 * 限量 参数
 * @author Rao
 * @Date 2021/12/03
 **/
public interface LimitWrapper<T> {

    /**
     * ID
     * @return
     */
    String markId();

    /**
     * 申请数
     * @return
     */
    long applyCount();

    /**
     * 限制
     */
    long limitCount();

    /**
     * 参数
     * @return
     */
    T params();

    /**
     * 限量类型
     */
    LimitedTypeEnum limitType();

}
