package com.shildon.knight.aop;

import java.lang.reflect.Method;

/**
 * 方法调用链接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 12, 2016 9:52:43 PM
 *
 */
public interface MethodInvocation {
	
	Object proceed() throws Throwable;
	
	Method getMethod();
	
	Object getTargetObject();
	
	Object[] getTargetParams();

}
