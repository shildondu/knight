package com.shildon.knight.task.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.shildon.knight.task.Executor;

/**
 * 定时任务执行者。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 3:34:19 PM
 *
 */
public class ScheduledExecutor implements Executor {
	
	private int time;
	private TimeUnit timeUnit;
	
	public ScheduledExecutor(int time, TimeUnit timeUnit) {
		this.time = time;
		this.timeUnit = timeUnit;
	}

	@Override
	public void run(Runnable runnable) {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, time, timeUnit);
	}
	
}
