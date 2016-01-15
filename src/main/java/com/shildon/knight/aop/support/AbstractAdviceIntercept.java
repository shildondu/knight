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
		Method targetMethod = methodInvocation.getMethod();
		Object targetObject = methodInvocation.getTargetObject();
		Object[] targetParams = methodInvocation.getTargetParams();
		Object result = null;

		if (filter(targetMethod, targetObject, targetParams)) {
			try {
				beforeMethod(targetMethod, targetObject, targetParams);
				result = methodInvocation.proceed();
				afterMethod(targetMethod, targetObject, targetParams);
			} catch (Throwable e) {
				afterException(targetMethod, targetObject, targetParams);
				e.printStackTrace();
			}
		}
		return result;
	}

	// 缺省方法实现
	@Override
	public boolean filter(Method method, Object targetObject, Object[] targetParams) {
		return true;
	}

	@Override
	public void beforeMethod(Method method, Object targetObject, Object[] targetParams) {
		
	}

	@Override
	public void afterMethod(Method method, Object targetObject, Object[] targetParams) {
		
	}

	@Override
	public void afterException(Method method, Object targetObject, Object[] targetParams) {
		
	}

}
