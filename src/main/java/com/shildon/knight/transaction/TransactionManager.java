package com.shildon.knight.transaction;

import java.sql.Connection;

/**
 * 事务管理接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 9:04:08 PM
 *
 */
public interface TransactionManager {
	
	// 获取数据库连接
	Connection getConnection();
	
	// 开启事务
	void begin();
	
	// 提交事务
	void commit();
	
	// 回滚事务
	void rollBack();
	
	// 关闭连接等
	void closeConnection();

}
