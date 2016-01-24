package com.shildon.knight.ioc.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shildon.knight.aop.MethodIntercept;
import com.shildon.knight.aop.annotation.AfterException;
import com.shildon.knight.aop.annotation.AfterMethod;
import com.shildon.knight.aop.annotation.BeforeMethod;
import com.shildon.knight.aop.annotation.Proxy;
import com.shildon.knight.aop.support.AbstractAdviceIntercept;
import com.shildon.knight.aop.support.CglibProxyFactory;
import com.shildon.knight.aop.support.ProxyMethod;
import com.shildon.knight.core.ClassScaner;
import com.shildon.knight.core.SpecifiedPackage;
import com.shildon.knight.ioc.BeanFactory;
import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.ioc.annotation.Inject;
import com.shildon.knight.transaction.annotation.Transaction;
import com.shildon.knight.transaction.support.JdbcTransactionManager;
import com.shildon.knight.transaction.support.TransactionAdviceIntercept;
import com.shildon.knight.util.BeanUtil;
import com.shildon.knight.util.ReflectUtil;

/**
 * 默认bean工厂，生产bean。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 3:21:48 PM
 *
 */
public class DefaultBeanFactory implements BeanFactory {

	// 缓存bean对象，默认所有bean都是单例
	private ConcurrentHashMap<String, Object> beanCache;
	// 缓存bean类，初始化后只读，所以不需要考虑并发
	private Map<String, Class<?>> beanClazzs;
	
	private static final Log log = LogFactory.getLog(DefaultBeanFactory.class);
	
	public DefaultBeanFactory() {
		init();
	}
	
	private void init() {
		beanCache = new ConcurrentHashMap<String, Object>();
		beanClazzs = ReflectUtil.getAnnotationClazzs(ClassScaner.loadClass(), Bean.class);
	}
	
	private <T> T getProxyBean(Class<?> clazz) {
		// 需要事务处理的方法
		final List<Method> transacationMethods = ReflectUtil.getAnnotationMethods(clazz, Transaction.class);
		List<Class<?>> clazzs = ClassScaner.loadClassBySpecify(SpecifiedPackage.INTERCEPTOR);
		// 代理方法集合
		List<ProxyMethod> beforeAdvices = new LinkedList<ProxyMethod>();
		List<ProxyMethod> afterAdvices = new LinkedList<ProxyMethod>();
		List<ProxyMethod> exceptionAdvices = new LinkedList<ProxyMethod>();
		// 拦截器链
		List<MethodIntercept> interceptors = new LinkedList<MethodIntercept>();
		
		if (log.isDebugEnabled()) {
			log.debug("proxy name: " + clazz.getName());
			log.debug("interceptors: " + clazzs);
		}
		
		// 配置事务处理拦截
		if (null != transacationMethods && 0 != transacationMethods.size()) {
			interceptors.add(new TransactionAdviceIntercept(new JdbcTransactionManager()) {
				
				@Override
				public boolean filter(Method method, Object targetObject, Object[] targetParams) {
					if (transacationMethods.contains(method)) {
						return true;
					} else {
						return false;
					}
				};
			});
		}
		
		// 初始化beforeAdvices, afterAdvices, exceptionAdvices
		for (Class<?> c : clazzs) {
			Method[] methods = c.getDeclaredMethods();
			
			for (Method method : methods) {
				
				if (method.isAnnotationPresent(BeforeMethod.class)) {
					Annotation beforeMethod = method.getAnnotation(BeforeMethod.class);
					String targetClass = null;
					String targetMethod = null;

					try {
						targetClass = (String) beforeMethod.annotationType().getMethod("clazz").invoke(beforeMethod);
						targetMethod = (String) beforeMethod.annotationType().getMethod("method").invoke(beforeMethod);
					} catch (IllegalArgumentException
							| InvocationTargetException
							| NoSuchMethodException
							| SecurityException | IllegalAccessException e) {
						log.error(e);
						e.printStackTrace();
					}
					
					if (clazz.getName().equals(targetClass)) {
						beforeAdvices.add(new ProxyMethod(targetMethod, method, method.getParameterTypes()));
					}
				} else if (method.isAnnotationPresent(AfterMethod.class)) {
					Annotation afterMethod = method.getAnnotation(AfterMethod.class);
					String targetClass = null;
					String targetMethod = null;

					try {
						targetClass = (String) afterMethod.annotationType().getMethod("clazz").invoke(afterMethod);
						targetMethod = (String) afterMethod.annotationType().getMethod("method").invoke(afterMethod);
					} catch (IllegalArgumentException
							| InvocationTargetException
							| NoSuchMethodException
							| SecurityException | IllegalAccessException e) {
						log.error(e);
						e.printStackTrace();
					}
					
					if (clazz.getName().equals(targetClass)) {
						afterAdvices.add(new ProxyMethod(targetMethod, method, method.getParameterTypes()));
					}
				} else if (method.isAnnotationPresent(AfterException.class)) {
					Annotation exceptionMethod = method.getAnnotation(AfterException.class);
					String targetClass = null;
					String targetMethod = null;

					try {
						targetClass = (String) exceptionMethod.annotationType().getMethod("clazz").invoke(exceptionMethod);
						targetMethod = (String) exceptionMethod.annotationType().getMethod("method").invoke(exceptionMethod);
					} catch (IllegalArgumentException
							| InvocationTargetException
							| NoSuchMethodException
							| SecurityException | IllegalAccessException e) {
						log.error(e);
						e.printStackTrace();
					}
					
					if (clazz.getName().equals(targetClass)) {
						exceptionAdvices.add(new ProxyMethod(targetMethod, method, method.getParameterTypes()));
					}
				}
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("beforeAdvices :" + beforeAdvices);
			log.debug("afterAdvices :" + afterAdvices);
			log.debug("exceptionAdvices :" + exceptionAdvices);
		}
		
		for (final ProxyMethod entry : beforeAdvices) {
			interceptors.add(new AbstractAdviceIntercept() {

				@Override
				public boolean filter(Method method,
						Object targetObject,
						Object[] targetParams) {
					if (method.getName().equals(entry.getMethodName())) {
						return true;
					} else {
						return false;
					}
				}
				
				@Override
				public void beforeMethod(Method method,
						Object targetObject,
						Object[] targetParams) {
					invokeMethod(entry);
				}
			});
		}

		for (final ProxyMethod entry : afterAdvices) {
			interceptors.add(new AbstractAdviceIntercept() {

				@Override
				public boolean filter(Method method,
						Object targetObject,
						Object[] targetParams) {
					if (method.getName().equals(entry.getMethodName())) {
						return true;
					} else {
						return false;
					}
				}
				
				@Override
				public void afterMethod(Method method,
						Object targetObject,
						Object[] targetParams) {
					invokeMethod(entry);
				}
			});
		}

		for (final ProxyMethod entry : exceptionAdvices) {
			interceptors.add(new AbstractAdviceIntercept() {

				@Override
				public boolean filter(Method method,
						Object targetObject,
						Object[] targetParams) {
					if (method.getName().equals(entry.getMethodName())) {
						return true;
					} else {
						return false;
					}
				}
				
				@Override
				public void afterException(Method method,
						Object targetObject,
						Object[] targetParams) {
					invokeMethod(entry);
				}
			});
		}
		return new CglibProxyFactory(clazz, interceptors).getProxy();
	}
	
	/*
	 * 执行代理方法
	 */
	private Object invokeMethod(ProxyMethod proxyMethod) {
		Object result = null;
		Object[] methodParams = null;

		if (null != proxyMethod.getMethodParams()) {
			methodParams = new Object[proxyMethod.getMethodParams().length];
			
			for (int i = 0; i < proxyMethod.getMethodParams().length; i++) {
				methodParams[i] = getBean(proxyMethod.getMethodParams()[i]);
			}
		}
		try {
			result = proxyMethod.getMethod().
					invoke(getBean(proxyMethod.getMethod().getDeclaringClass()), methodParams);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error(e);
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据class获取bean
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> type) {
		T t = (T) beanCache.get(type.getName());
		
		if (null == t) {
			Class<T> clazz = (Class<T>) beanClazzs.get(type.getName());
			
			if (null != clazz) {

				try {

					if (clazz.isAnnotationPresent(Proxy.class)) {
						t = getProxyBean(clazz);
					} else {
						t = (T) BeanUtil.instantiateBean(clazz);
					}
					// 先放进缓存再初始化，避免循环引用导致的死循环
					beanCache.put(clazz.getName(), t);
					// 初始化bean，依赖注入的地方
					initiateBean(t);

					if (log.isDebugEnabled()) {
						log.debug("instantiate " + type.getName() + " successfully!");
					}
					
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("Instantiate " + type.getName() + " fail!", e);
				}
			} else {
				// TODO
			}
		}
		return t;
	}
	
	/**
	 * 初始化bean
	 * @param bean
	 */
	private void initiateBean(Object bean) {
		Class<?> clazz = bean.getClass();
		List<Field> injectFields = ReflectUtil.getAnnotationFields(clazz, Inject.class);
		
		for (Field field : injectFields) {
			Class<?> type = field.getType();
			Object result = getBean(type);
			
			// 如果不存在，则获取其子类型
			if (null == result) {
				for (Class<?> c : beanClazzs.values()) {
					List<Class<?>> interfaces = Arrays.asList(c.getInterfaces());
					if (c.getSuperclass() == c || interfaces.contains(type)) {
						result = getBean(c);
						break;
					}
				}
			}

			try {
				field.set(bean, getBean(type));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
	}

}
