package com.shildon.knight.aop.support;

import java.lang.reflect.Method;

import com.shildon.knight.aop.Advice;
import com.shildon.knight.aop.MethodIntercept;
import com.shildon.knight.aop.MethodInvocation;

/**
 * 抽象拦截器模板类。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 3:08:11 PM
 *
 */
public class AbstractAdviceIntercept implements MethodIntercept, Advice {
	
	@Override
	public Object invoke(MethodInvocation methodInvocation) {
		Object result = null;

		if (filter(methodInvocation.getMethod())) {
			try {
				beforeMethod();
				result = methodInvocation.proceed();
				afterMethod();
			} catch (Throwable e) {
				afterException();
				e.printStackTrace();
			}
		}
		return result;
	}

	// 缺省方法实现
	@Override
	public boolean filter(Method method) {
		return true;
	}

	@Override
	public void beforeMethod() {
		
	}

	@Override
	public void afterMethod() {
		
	}

	@Override
	public void afterException() {
		
	}

}
