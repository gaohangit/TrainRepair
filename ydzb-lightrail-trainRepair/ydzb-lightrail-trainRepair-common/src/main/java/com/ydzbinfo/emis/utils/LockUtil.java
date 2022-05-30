package com.ydzbinfo.emis.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 加锁工具类
 * @author 张天可
 * @since 2021/9/30
 */
public class LockUtil {

    @FunctionalInterface
    public interface DoLock {
        void lock();
    }

    private LockUtil() {

    }

    public static LockUtil newInstance() {
        return new LockUtil();
    }

    private final Map<Object, ReentrantLock> LOCK_MAP = new HashMap<>();

    /**
     * 按照key进行加锁，这样key不同时，就不会因为不必要的同步执行而丧失性能
     * 因为不能在synchronized方法内部进行加锁，所以返回一个进行加锁的Functional对象
     *
     * @param key 键值
     */
    synchronized public DoLock getDoLock(Object key) {
        if (!LOCK_MAP.containsKey(key)) {
            LOCK_MAP.putIfAbsent(key, new ReentrantLock());
        }
        return () -> LOCK_MAP.get(key).lock();
    }

    /**
     * 按照key进行解锁
     *
     * @param key 键值
     */
    synchronized public void unlock(Object key) {
        if (LOCK_MAP.containsKey(key)) {
            ReentrantLock locker = LOCK_MAP.get(key);
            locker.unlock();
            if (!locker.isLocked() && locker.getQueueLength() == 0) {
                LOCK_MAP.remove(key);
            }
        }
    }

    /**
     * 释放key的当前线程的全部锁
     *
     * @param key 键值
     */
    synchronized public void unlockAll(Object key) {
        if (LOCK_MAP.containsKey(key)) {
            ReentrantLock locker = LOCK_MAP.get(key);
            while (locker.isHeldByCurrentThread()) {
                locker.unlock();
            }
            if (!locker.isLocked() && locker.getQueueLength() == 0) {
                LOCK_MAP.remove(key);
            }
        }
    }

}
