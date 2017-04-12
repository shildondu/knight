package com.shildon.knight.aop;

/**
 * 方法拦截接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 12, 2016 9:51:30 PM
 *
 */
public interface MethodInvocator {

	Object invoke(MethodInvocation methodInvocation);
	
}
