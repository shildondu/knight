package com.shildon.knight.aop.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 7:39:06 PM
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeMethod {

	String clazz();
	String method();

}
