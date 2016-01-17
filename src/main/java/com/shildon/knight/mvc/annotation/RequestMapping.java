package com.shildon.knight.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * url匹配。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 2:58:25 PM
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

	String value();

}
