package com.shildon.knight.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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

	private static ClassScaner annotationLoader = new ClassScaner();
	
	/**
	 * 获取指定Annotation的类
	 * @param annotationClass
	 * @return
	 */
	public static Map<String, Class<?>> getAnnotationClazzs(Class<?> annotationClass) {
		List<Class<?>> clazzs = null;
		Map<String, Class<?>> annotationClazzs = new HashMap<String, Class<?>>();
		clazzs = annotationLoader.loadClass();

		for (Class<?> clazz : clazzs) {
			Annotation[] annotations = clazz.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotationClass == annotation.annotationType()) {
					annotationClazzs.put(clazz.getName(), clazz);
					break;
				}
			}
		}
		return annotationClazzs;
	}

	/**
	 * 获取制定Annotation的成员变量
	 * @param clazz
	 * @param annotationClass
	 * @return
	 */
	public static Field[] getAnnotationFields(Class<?> clazz, Class<?> annotationClass) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> annotationFields = new LinkedList<Field>();
		int i = 0;
		
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			
			for (Annotation annotation : annotations) {
				
				if (annotation.annotationType() == annotationClass) {
					// 设置私有域可访问
					field.setAccessible(true);
					annotationFields.add(field);
					i++;
					break;
				}
			}
		}
		Field[] returnFields = new Field[i];
		return annotationFields.toArray(returnFields);
	}

}
