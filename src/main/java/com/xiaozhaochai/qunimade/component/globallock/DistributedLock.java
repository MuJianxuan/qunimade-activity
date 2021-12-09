package com.xiaozhaochai.qunimade.component.globallock;

import com.xiaozhaochai.qunimade.component.globallock.lock.LockWrapper;

import java.util.function.Supplier;

/**
 * @author Rao
 * @Date 2021/12/06
 **/
public interface DistributedLock {

    /**
     * tryLock
     * @param lockWrapper
     * @param successLockSupplier
     * @param failLockSupplier
     * @param <T>
     * @return
     */
    <T> T tryLock(LockWrapper lockWrapper, Supplier<T> successLockSupplier, Supplier<T> failLockSupplier);

}
