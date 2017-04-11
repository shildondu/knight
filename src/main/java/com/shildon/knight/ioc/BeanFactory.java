package com.shildon.knight.ioc;

/**
 * Bean工厂接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 6, 2016 2:49:00 PM
 *
 */
public interface BeanFactory {
	
	<T> T getBean(Class<T> type);
	
}
