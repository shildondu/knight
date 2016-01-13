package com.shildon.knight.ioc;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.ioc.annotation.Inject;

/**
 * Bean工厂，生产bean。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 6, 2016 2:49:00 PM
 *
 */
public class BeanFactory {
	
	// 缓存bean对象，默认所有bean都是单例
	private ConcurrentHashMap<String, Object> beanCache;
	// 缓存bean类，初始化后只读，所以不需要考虑并发
	private Map<String, Class<?>> beanClazzs;
	
	public BeanFactory() {
		beanCache = new ConcurrentHashMap<String, Object>();
		beanClazzs = ReflectUtil.getAnnotationClazzs(Bean.class);
	}
	
	private static final Log log = LogFactory.getLog(BeanFactory.class);
	
	/**
	 * 根据class获取bean
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
