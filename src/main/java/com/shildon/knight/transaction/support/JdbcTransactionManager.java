package com.shildon.knight.transaction.support;

import java.sql.Connection;
import java.sql.SQLException;

import com.shildon.knight.orm.ConnectionHolder;
import com.shildon.knight.transaction.TransactionManager;

/**
 * JDBC事务管理。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 9:06:07 PM
 *
 */
public class JdbcTransactionManager  implements TransactionManager {
	
	private Connection connection;

	@Override
	public void begin() {
		connection = ConnectionHolder.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void rollBack() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		ConnectionHolder.closeConnection();
	}
	
}
