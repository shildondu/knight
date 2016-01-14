package com.shildon.knight.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务注解。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 3:28:12 PM
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

	// 间隔时间
	int time();
	// 默认是秒
	TimeUnit timeUnit() default TimeUnit.SECONDS;
	
}
