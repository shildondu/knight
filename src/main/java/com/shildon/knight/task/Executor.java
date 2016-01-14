package com.shildon.knight.task;

/**
 * 任务执行者。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 3:36:13 PM
 *
 */
public interface Executor {

	public void run(Runnable runnable);
	
}
