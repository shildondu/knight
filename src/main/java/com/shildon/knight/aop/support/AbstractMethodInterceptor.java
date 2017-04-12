package com.shildon.knight.aop.support;

import java.lang.reflect.Method;

import com.shildon.knight.aop.Advice;
import com.shildon.knight.aop.MethodInvocator;
import com.shildon.knight.aop.MethodInvocation;

/**
 * 抽象拦截器模板类。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 3:08:11 PM
 *
 */
public abstract class AbstractMethodInterceptor implements MethodInvocator, Advice {

	protected ProxyMethod proxyMethod;

	public AbstractMethodInterceptor(ProxyMethod proxyMethod) {
		this.proxyMethod = proxyMethod;
	}

	public AbstractMethodInterceptor() {}
	
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
			} finally {
				doFinally(targetMethod, targetObject, targetParams);
			}
		}
		return result;
	}
	
	protected void doFinally(Method targetMethod, Object targetObject, Object[] targetParams) {
		
	}

	// 缺省方法实现
	@Override
	public boolean filter(Method targetMethod, Object targetObject, Object[] targetParams) {
		return null == proxyMethod ? true : targetMethod.getName().equals(proxyMethod.getTargetMethodName());
	}

	@Override
	public void beforeMethod(Method targetMethod, Object targetObject, Object[] targetParams) {
		
	}

	@Override
	public void afterMethod(Method targetMethod, Object targetObject, Object[] targetParams) {
		
	}

	@Override
	public void afterException(Method targetMethod, Object targetObject, Object[] targetParams) {
		
	}

}
