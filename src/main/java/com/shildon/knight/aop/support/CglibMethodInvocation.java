package com.shildon.knight.aop.support;

import net.sf.cglib.proxy.MethodProxy;

/**
 * Cglib的动态代理方法调用链。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 8:53:43 PM
 *
 */
public class CglibMethodInvocation extends AbstractMethodInvocation {
	
	private MethodProxy methodProxy;

	@Override
	protected Object execute() throws Throwable {
		Object result = methodProxy.invokeSuper(targetObject, targetParams);
		return result;
	}

	/* =============== getter and setter ================ */
	public MethodProxy getMethodProxy() {
		return methodProxy;
	}

	public CglibMethodInvocation setMethodProxy(MethodProxy methodProxy) {
		this.methodProxy = methodProxy;
		return this;
	}

}
