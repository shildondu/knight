package com.shildon.knight.aop.support;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.shildon.knight.aop.MethodIntercept;
import com.shildon.knight.aop.ProxyFactory;

/**
 * Cglib动态代理工厂，生产代理对象。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 9:02:04 PM
 *
 */
public class CglibProxyFactory implements ProxyFactory {

	private Class<?> targetClass;
	private List<MethodIntercept> interceptors;
	
	public CglibProxyFactory(Class<?> targetClass, List<MethodIntercept> interceptors) {
		this.targetClass = targetClass;
		this.interceptors = interceptors;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy() {
		Object result = Enhancer.create(targetClass, new MethodInterceptor() {
			
			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				return new CglibMethodInvocation().setMethodProxy(proxy).setTargetClass(targetClass).
						setInterceptors(interceptors).setTargetObject(obj).setTargetParams(args).
						setTargetMethod(method).proceed();
			}
		});
		return (T) result;
	}

}
