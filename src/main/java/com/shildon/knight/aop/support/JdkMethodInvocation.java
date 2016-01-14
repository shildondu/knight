package com.shildon.knight.aop.support;

/**
 * Jdk的动态代理方法调用链。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 8:43:52 PM
 *
 */
public class JdkMethodInvocation extends AbstractMethodInvocation {

	@Override
	protected Object execute() throws Throwable {
		Object result = targetMethod.invoke(targetObject, targetParams);
		return result;
	}

}
