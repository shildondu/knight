package com.shildon.knight.aop;

/**
 * 代理工厂。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 13, 2016 2:56:18 PM
 *
 */
public interface ProxyFactory {

	<T> T getProxy();
	
}
