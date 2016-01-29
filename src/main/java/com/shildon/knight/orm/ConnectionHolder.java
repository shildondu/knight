package com.shildon.knight.orm;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 持有数据库连接，使其线程安全。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 9:08:28 PM
 *
 */
public class ConnectionHolder {
	
	private static ThreadLocal<Connection> localConnecton = new ThreadLocal<Connection>();
	
	/*
	 * 获取数据库连接
	 */
	public static Connection getConnection() {
		Connection connection = localConnecton.get();
		
		if (null == connection) {
			DruidDataSource dataSource = null;
			try {
				dataSource = new DruidDataSource();
				// TODO
				dataSource.setDriverClassName("com.mysql.jdbc.Driver");
				dataSource.setUrl("jdbc:mysql://localhost:3306/shildon");
				dataSource.setUsername("shildon");
				dataSource.setPassword("duxiaodong11");
				connection = dataSource.getConnection();
				localConnecton.set(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dataSource.close();
			}
		}
		return connection;
	}
	
	/*
	 * 关闭数据库连接
	 */
	public static void closeConnection() {
		Connection connection = localConnecton.get();
		if (null != connection) {
			try {
				connection.close();
				localConnecton.set(null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
