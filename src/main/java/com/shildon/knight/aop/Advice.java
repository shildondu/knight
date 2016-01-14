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
	public boolean filter(Method method);
	
	public void beforeMethod();
	
	public void afterMethod();

	public void afterException();
	
}
