package com.shildon.knight.orm;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * 数据库操作接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 30, 2016
 */
public interface DbOperation {
	
	// 保存
	boolean save(Object t);
	
	// 删除
	boolean delete(Object t);
	
	// 查询
	Object find(Serializable id);
	
	// 更新
	boolean update(Object t);
	
	// 执行查询sql语句
	ResultSet executeQuery(String sql);
	
	// 执行修改sql语句
	boolean execute(String sql);

}
