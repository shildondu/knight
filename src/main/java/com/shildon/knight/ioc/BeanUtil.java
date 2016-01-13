package com.shildon.knight.ioc;

/**
 * bean工具类
 * @author shildon<shildondu@gmail.com>
 * @date Jan 12, 2016 8:39:19 PM
 *
 */
public class BeanUtil {
	
	public static Object instantiateBean(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		// TODO 可优化，用策略模式指定实例化策略
		return clazz.newInstance();
	}

}
