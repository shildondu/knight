package com.shildon.knight.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
	 * 使用公有参构造方法实例化指定类
	 * @param clazz 类
	 * @return bean
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object instantiateBean(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
	    Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
	    	if (0 == constructor.getParameterCount()) {
	    	    return constructor.newInstance();
			}
		}
		return null;
	}

	/**
	 * 获取clazzs中注有annotation的类。
	 * @param clazzs 类集
	 * @param annotation 指定的annotation
	 * @return 方法集合
	 */
	public static List<Method> getAnnotationMethods(List<Class<?>> clazzs, Class<? extends Annotation> annotation) {
		List<Method> annotationMethods = new LinkedList<>();
		for (Class<?> clazz : clazzs) {
		    annotationMethods.addAll(getAnnotationMethods(clazz, annotation));
		}
		return annotationMethods;
	}

	/**
	 * 获取clazzs中注有annotation的类。
	 * @param clazzs 类集
	 * @param annotation 指定的annotation
	 * @return <类名, 类>
	 */
	public static Map<String, Class<?>> getAnnotationClazzs(List<Class<?>> clazzs, Class<? extends Annotation> annotation) {
		Map<String, Class<?>> annotationClazzs = new HashMap<>();

		for (Class<?> clazz : clazzs) {
			if (clazz.isAnnotationPresent(annotation)) {
				annotationClazzs.put(clazz.getName(), clazz);
			}
		}
		return annotationClazzs;
	}
	
	/**
	 * 获取clazz中注有annotation的方法。
	 * @param clazz 类
	 * @param annotation 指定的annotation
	 * @return 方法集合
	 */
	public static List<Method> getAnnotationMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
		Method[] methods = clazz.getDeclaredMethods();
		List<Method> annotationMethods = new LinkedList<>();
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotation)) {
				method.setAccessible(true);
				annotationMethods.add(method);
			}
		}
		return annotationMethods;
	}

	/**
	 * 获取指定Annotation的成员变量
	 * @param clazz 类
	 * @param annotation 指定的annotation
	 * @return 成员变量集合
	 */
	public static List<Field> getAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotation) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> annotationFields = new LinkedList<>();
		
		for (Field field : fields) {
			if (field.isAnnotationPresent(annotation)) {
				// 设置私有域可访问
				field.setAccessible(true);
				annotationFields.add(field);
			}
		}
		return annotationFields;
	}

}
