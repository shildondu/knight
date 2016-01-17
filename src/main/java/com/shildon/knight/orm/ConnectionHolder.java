package com.shildon.knight.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
			//TODO
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/shildon", 
						"shildon", "duxiaodong11");
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
