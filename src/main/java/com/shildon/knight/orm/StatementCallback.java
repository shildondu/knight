package com.shildon.knight.orm;

import java.sql.Statement;

/**
 * Statement回调接口。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 30, 2016
 */
public interface StatementCallback<T> {
	
	public T doInStatement(Statement statement);

}
