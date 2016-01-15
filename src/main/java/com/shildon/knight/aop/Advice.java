package com.shildon.knight.aop;

import java.lang.reflect.Method;

/**
 * 增强/通知接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 12, 2016 9:50:05 PM
 *
 */
public interface Advice {

	// 实现方法过滤
	public boolean filter(Method method, Object targetObject, Object[] targetParams);
	
	public void beforeMethod(Method method, Object targetObject, Object[] targetParams);
	
	public void afterMethod(Method method, Object targetObject, Object[] targetParams);

	public void afterException(Method method, Object targetObject, Object[] targetParams);
	
}
