package com.ydzbinfo.emis.utils.mybatisplus.param;

/**
 * 用于标记可进行逻辑连接的对象
 *
 * @author 张天可
 * @since 2021/12/24
 */
public interface LogicalLinkable<T> {
    Class<T> getMainClass();

    boolean test(T entity);
}
