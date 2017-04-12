package com.shildon.knight.aop.support;

import java.lang.reflect.Method;

/**
 * 用来封装代理方法的信息。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 8:49:19 PM
 *
 */
public class ProxyMethod {
	
	// 被代理方法的名字
	private String targetMethodName;
	// 代理方法
	private Method method;
	// 代理方法的方法参数
	private Class<?>[] methodParams;
	
	public ProxyMethod(String targetMethodName, Method method) {
		this.targetMethodName = targetMethodName;
		this.method = method;
	}

	public ProxyMethod(String targetMethodName, Method method, Class<?>[] methodParams) {
		this.targetMethodName = targetMethodName;
		this.method = method;
		this.methodParams = methodParams;
	}

	public String getTargetMethodName() {
		return targetMethodName;
	}
	public void setTargetMethodName(String targetMethodName) {
		this.targetMethodName = targetMethodName;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?>[] getMethodParams() {
		return methodParams;
	}

	public void setMethodParams(Class<?>[] methodParams) {
		this.methodParams = methodParams;
	}

}
