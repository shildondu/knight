package com.shildon.knight.aop.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.shildon.knight.aop.MethodIntercept;
import com.shildon.knight.aop.ProxyFactory;

/**
 * Jdk动态代理工厂，生产代理对象。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 9:23:27 PM
 *
 */
public class JdkProxyFactory implements ProxyFactory {

	private Class<?> targetClass;
	// Jdk需要传入被代理的对象
	private Object targetObject;
	private List<MethodIntercept> interceptors;
	
	public JdkProxyFactory(Class<?> targetClass, Object targetObject, List<MethodIntercept> interceptors) {
		this.targetClass = targetClass;
		this.targetObject = targetObject;
		this.interceptors = interceptors;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy() {
		Object result = Proxy.newProxyInstance(targetClass.getClassLoader(),
				targetClass.getInterfaces(), new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				// 这里proxy并不是原来的对象
				return new JdkMethodInvocation().setTargetClass(targetClass).setInterceptors(interceptors).
						setTargetObject(targetObject).setTargetMethod(method).setTargetParams(args).proceed();
			}
		});
		return (T) result;
	}

}
