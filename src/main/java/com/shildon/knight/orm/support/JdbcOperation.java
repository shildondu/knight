package com.shildon.knight.orm.support;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.shildon.knight.orm.DbOperation;
import com.shildon.knight.orm.StatementCallback;
import com.shildon.knight.transaction.TransactionManager;
import com.shildon.knight.transaction.support.JdbcTransactionManager;

/**
 * jdbc操作类。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 30, 2016
 */
public class JdbcOperation implements DbOperation {
	
	public <T> T execute(StatementCallback<T> callback) {
		TransactionManager transactionManager = new JdbcTransactionManager();
		Connection connection = transactionManager.getConnection();
		Statement statement = null;
		T t = null;

		try {
			statement = connection.createStatement();
			transactionManager.begin();
			t = callback.doInStatement(statement);
			transactionManager.commit();
		} catch (SQLException e) {
			transactionManager.rollBack();
			e.printStackTrace();
		} finally {
			transactionManager.closeConnection();
		}
		return t;
	}

	@Override
	public boolean save(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet executeQuery(final String sql) {
		return execute(new StatementCallback<ResultSet>() {
			
			@Override
			public ResultSet doInStatement(Statement statement) {
				ResultSet resultSet = null;
				try {
					resultSet = statement.executeQuery(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return resultSet;
			}
		});
	}

	@Override
	public boolean execute(final String sql) {
		return execute(new StatementCallback<Boolean>() {

			@Override
			public Boolean doInStatement(Statement statement) {
				boolean result = false;
				try {
					result = statement.execute(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

}
