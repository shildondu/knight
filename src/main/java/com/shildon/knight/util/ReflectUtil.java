package com.shildon.knight.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 * @author shildon<shildondu@gmail.com>
 * @date Jan 12, 2016 8:38:39 PM
 *
 */
public class ReflectUtil {

	/**
	 * 获取指定Annotation的类
	 * @param annotationClass
	 * @return
	 */
	public static Map<String, Class<?>> getAnnotationClazzs(List<Class<?>> clazzs, Class<? extends Annotation> annotationClass) {
		Map<String, Class<?>> annotationClazzs = new HashMap<String, Class<?>>();

		for (Class<?> clazz : clazzs) {
			if (clazz.isAnnotationPresent(annotationClass)) {
				annotationClazzs.put(clazz.getName(), clazz);
			}
		}
		return annotationClazzs;
	}
	
	/**
	 * 获取指定Annotation的方法。
	 * @param clazz
	 * @param annotationClass
	 * @return
	 */
	public static Method[] getAnnotationMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Method[] methods = clazz.getDeclaredMethods();
		List<Method> annotationMethods = new LinkedList<Method>();
		int i = 0;
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				method.setAccessible(true);
				annotationMethods.add(method);
				i++;
			}
		}
		Method[] returnMethods = new Method[i];
		return annotationMethods.toArray(returnMethods);
	}

	/**
	 * 获取指定Annotation的成员变量
	 * @param clazz
	 * @param annotationClass
	 * @return
	 */
	public static Field[] getAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> annotationFields = new LinkedList<Field>();
		int i = 0;
		
		for (Field field : fields) {
			if (field.isAnnotationPresent(annotationClass)) {
				// 设置私有域可访问
				field.setAccessible(true);
				annotationFields.add(field);
				i++;
			}
		}
		Field[] returnFields = new Field[i];
		return annotationFields.toArray(returnFields);
	}

}
