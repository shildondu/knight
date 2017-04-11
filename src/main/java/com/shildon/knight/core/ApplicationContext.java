package com.shildon.knight.core;

/**
 * 应用上下文接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 24, 2016
 */
public interface ApplicationContext {
	
	<T> T getBean(Class<T> type);

}
