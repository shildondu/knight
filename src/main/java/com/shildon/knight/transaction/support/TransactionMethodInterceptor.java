package com.shildon.knight.transaction.support;

import java.lang.reflect.Method;

import com.shildon.knight.aop.support.AbstractMethodInterceptor;
import com.shildon.knight.aop.support.ProxyMethod;
import com.shildon.knight.transaction.TransactionManager;

/**
 * 利用AOP实现事务管理。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 9:46:03 PM
 *
 */
public class TransactionMethodInterceptor extends AbstractMethodInterceptor {
	
	private TransactionManager transactionManager;
	
	public TransactionMethodInterceptor(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public void beforeMethod(Method method, Object targetObject,
			Object[] targetParams) {
		transactionManager.begin();
	}
	
	@Override
	public void afterMethod(Method method, Object targetObject,
			Object[] targetParams) {
		transactionManager.commit();
	}
	
	@Override
	public void afterException(Method method, Object targetObject,
			Object[] targetParams) {
		transactionManager.rollBack();
	}
	
	@Override
	protected void doFinally(Method method, Object targetObject,
			Object[] targetParams) {
		transactionManager.closeConnection();
	}

}
