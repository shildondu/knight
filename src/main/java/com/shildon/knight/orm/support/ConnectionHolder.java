package com.shildon.knight.orm.support;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.shildon.knight.util.PropertiesUtil;

/**
 * 持有数据库连接，使其线程安全。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 15, 2016 9:08:28 PM
 *
 */
public class ConnectionHolder {
	
	private static final String DEFAULT_PROPERTITES = "jdbc.properties";
	private static final String DRIVER_NAME = "driverName";
	private static final String URL_NAME = "url";
	private static final String USERNAME_NAME = "username";
	private static final String PASSWORD_NAME = "password";
	
	private static ThreadLocal<Connection> localConnecton = new ThreadLocal<>();
	private static String driverClassName;
	private static String url;
	private static String username;
	private static String password;
	
	static {
		InputStream is = Thread.currentThread().
				getContextClassLoader().getResourceAsStream(DEFAULT_PROPERTITES);
		PropertiesUtil propertiesUtil = new PropertiesUtil(is);
		driverClassName = (String) propertiesUtil.getValue(DRIVER_NAME);
		url = (String) propertiesUtil.getValue(URL_NAME);
		username = (String) propertiesUtil.getValue(USERNAME_NAME);
		password = (String) propertiesUtil.getValue(PASSWORD_NAME);
	}
	
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
				dataSource.setDriverClassName(driverClassName);
				dataSource.setUrl(url);
				dataSource.setUsername(username);
				dataSource.setPassword(password);
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
