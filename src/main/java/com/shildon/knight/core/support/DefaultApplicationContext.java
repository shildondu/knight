package com.shildon.knight.core.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shildon.knight.core.ApplicationContext;
import com.shildon.knight.core.ClassScaner;
import com.shildon.knight.core.SpecifiedPackage;
import com.shildon.knight.ioc.BeanFactory;
import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.ioc.support.DefaultBeanFactory;
import com.shildon.knight.task.Executor;
import com.shildon.knight.task.annotation.Scheduled;
import com.shildon.knight.task.support.ScheduledExecutor;
import com.shildon.knight.util.ReflectUtil;

/**
 * 应用上下文。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 24, 2016
 */
public class DefaultApplicationContext implements ApplicationContext {
	
	private BeanFactory beanFactory;
	
	private static final Log log = LogFactory.getLog(DefaultApplicationContext.class);
	
	public DefaultApplicationContext() {
		beanFactory = new DefaultBeanFactory();
		startScheduledTask();
	}

	/**
	 * 开启定时任务
	 */
	private void startScheduledTask() {
		Map<String, Class<?>> tasks = ReflectUtil.
				getAnnotationClazzs(ClassScaner.loadClassBySpecify(SpecifiedPackage.SCHEDULE.getPackageName()), Bean.class);
		
		if (log.isDebugEnabled()) {
			log.debug("tasks: " + tasks);
		}
		
		for (final Class<?> clazz : tasks.values()) {
			List<Method> methods = ReflectUtil.getAnnotationMethods(clazz, Scheduled.class);
			
			for (final Method method : methods) {
				Scheduled scheduled = method.getAnnotation(Scheduled.class);
				int time = 1;
				TimeUnit timeUnit = null;

				try {
					time = scheduled.time();
					timeUnit = scheduled.timeUnit();
				
					/**
					if (null == timeUnit) {
						// 获取默认值
						timeUnit = (TimeUnit) annotation.annotationType().getMethod("timeUnit").getDefaultValue();
					}
					**/
				} catch (IllegalArgumentException | SecurityException e) {
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
							method.invoke(beanFactory.getBean(clazz));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							log.error(e);
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	@Override
	public <T> T getBean(Class<T> type) {
		return beanFactory.getBean(type);
	}
}
