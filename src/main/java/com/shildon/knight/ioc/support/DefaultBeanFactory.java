package com.shildon.knight.ioc.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shildon.knight.core.ClassScaner;
import com.shildon.knight.core.SpecifiedPackage;
import com.shildon.knight.ioc.BeanFactory;
import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.ioc.annotation.Inject;
import com.shildon.knight.task.Executor;
import com.shildon.knight.task.annotation.Scheduled;
import com.shildon.knight.task.support.ScheduledExecutor;
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
		startScheduledTask();
	}
	
	private void init() {
		beanCache = new ConcurrentHashMap<String, Object>();
		beanClazzs = ReflectUtil.getAnnotationClazzs(ClassScaner.loadClass(), Bean.class);
	}
	
	private void startScheduledTask() {
		Map<String, Class<?>> tasks = ReflectUtil.
				getAnnotationClazzs(ClassScaner.loadClassBySpecify(SpecifiedPackage.SCHEDULE), Bean.class);
		
		if (log.isDebugEnabled()) {
			log.debug("tasks: " + tasks);
		}
		
		for (final Class<?> clazz : tasks.values()) {
			Method[] methods = ReflectUtil.getAnnotationMethods(clazz, Scheduled.class);
			
			for (final Method method : methods) {
				Annotation annotation = method.getAnnotation(Scheduled.class);
				int time = 1;
				TimeUnit timeUnit = null;

				try {
					time = (int) annotation.annotationType().getMethod("time").invoke(annotation);
					timeUnit = (TimeUnit) annotation.annotationType().getMethod("timeUnit").invoke(annotation);
					
					if (null == timeUnit) {
						// 获取默认值
						timeUnit = (TimeUnit) annotation.annotationType().getMethod("timeUnit").getDefaultValue();
					}
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException e) {
					log.error(e);
					e.printStackTrace();
				}
				
				if (log.isDebugEnabled()) {
					log.debug("scheduled time: " + time);
					log.debug("scheduled unit: " + timeUnit.toString());
				}
				
				// 调用执行器执行定时任务
				Executor executor = new ScheduledExecutor(time, timeUnit);
				executor.run(new Runnable() {
					
					@Override
					public void run() {
						// 无参方法
						try {
							method.invoke(getBean(clazz));
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							log.error(e);
							e.printStackTrace();
						}
					}
				});
			}
		}
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
					t = (T) BeanUtil.instantiateBean(clazz);
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
		Field[] injectFields = ReflectUtil.getAnnotationFields(clazz, Inject.class);
		
		for (Field field : injectFields) {
			Class<?> type = field.getType();
			try {
				field.set(bean, getBean(type));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(e);
				e.printStackTrace();
			}
		}
	}

}
