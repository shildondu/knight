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
	private String methodName;
	// 代理方法
	private Method method;
	// 代理方法的方法参数
	private Class<?>[] methodParams;
	
	public ProxyMethod(String methodName, Method method) {
		this.methodName = methodName;
		this.method = method;
	}
	
	public ProxyMethod(String methodNmae, Method method, Class<?>[] methodParams) {
		this.methodName = methodNmae;
		this.method = method;
		this.methodParams = methodParams;
	}

	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
