package com.shildon.knight.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 7:42:33 PM
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterException {

	String clazz();
	String method();

}
