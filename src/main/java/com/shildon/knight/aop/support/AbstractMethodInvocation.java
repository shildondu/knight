package com.shildon.knight.aop.support;

import java.lang.reflect.Method;
import java.util.List;

import com.shildon.knight.aop.MethodIntercept;
import com.shildon.knight.aop.MethodInvocation;

/**
 * 方法调用链抽象模板类。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 8:25:40 PM
 *
 */
public abstract class AbstractMethodInvocation implements MethodInvocation {
	
	protected Class<?> targetClass;
	protected Object targetObject;
	protected Method targetMethod;
	protected Object[] targetParams;
	
	protected List<MethodIntercept> interceptors;
	protected int index = -1;
	
	protected abstract Object execute() throws Throwable;

	@Override
	public Object proceed() throws Throwable {
		if (this.index == this.interceptors.size() - 1) {
			return execute();
		} else {
			if (this.index < this.interceptors.size()) {
				return this.interceptors.get(++this.index).invoke(this);
			} else {
				return execute();
			}
		}
	}

	@Override
	public Method getMethod() {
		return this.targetMethod;
	}

	/* --------------- getter and setter --------------- */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	public AbstractMethodInvocation setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
		return this;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public AbstractMethodInvocation setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
		return this;
	}

	public AbstractMethodInvocation setTargetMethod(Method targetMethod) {
		this.targetMethod = targetMethod;
		return this;
	}

	public Object[] getTargetParams() {
		return targetParams;
	}

	public AbstractMethodInvocation setTargetParams(Object[] targetParams) {
		this.targetParams = targetParams;
		return this;
	}

	public List<MethodIntercept> getInterceptors() {
		return interceptors;
	}

	public AbstractMethodInvocation setInterceptors(List<MethodIntercept> interceptors) {
		this.interceptors = interceptors;
		return this;
	}

}
