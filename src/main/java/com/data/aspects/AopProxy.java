package com.data.aspects;

import org.springframework.aop.framework.AopContext;

/**
 * 通用自身代理获取
 *
 * @author azhuzhu 2020/7/11 14:40.
 */
public interface AopProxy<T> {

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) AopContext.currentProxy();
    }
}
