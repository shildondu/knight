package com.shildon.knight.mvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.shildon.knight.core.ApplicationContext;
import com.shildon.knight.core.ClassScaner;
import com.shildon.knight.core.SpecifiedPackage;
import com.shildon.knight.core.support.WebApplicationContext;
import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.mvc.annotation.RequestMapping;
import com.shildon.knight.util.BeanUtil;
import com.shildon.knight.util.ReflectUtil;

/**
 * 请求分发器。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 3:31:57 PM
 *
 */
public class DispatcherServlet extends HttpServlet {

	private ApplicationContext webApplicationContext;
	private Map<String, Method> requestMap;
	
	private static final Log log = LogFactory.getLog(DispatcherServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 获取上下文
		webApplicationContext = (WebApplicationContext) config.getServletContext().getAttribute(ContextLoader.WEB_ROOT);
		// 初始化requestMap
		Map<String, Class<?>> clazzs = ReflectUtil.
				getAnnotationClazzs(ClassScaner.loadClassBySpecify(SpecifiedPackage.CONTROLLER), Bean.class);
		
		for (Class<?> clazz : clazzs.values()) {
			List<Method> handlerMethods = ReflectUtil.getAnnotationMethods(clazz, RequestMapping.class);
			
			for (Method method : handlerMethods) {
				Annotation annotation = method.getAnnotation(RequestMapping.class);
				String uri = null;

				try {
					uri = (String) annotation.annotationType().getMethod("value").invoke(annotation);
					requestMap.put(uri, method);
					
					if (log.isDebugEnabled()) {
						log.debug(method.getName() + " -> " + uri);
					}
					
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("init dispatcher servlet successfully!");
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.service(request, response);
		// 获取请求uri
		String requestUri = request.getRequestURI().
				substring(request.getContextPath().length());
		// TODO
		Map<String, String[]> requestParameters = request.getParameterMap();
		Method method = requestMap.get(requestUri);
		
		if (null == method) {
			// TODO 404
			response.setContentType("text/html");
			response.getWriter().write("404 not found~");
			return;
		}
		Class<?>[] methodParameterClazzs = method.getParameterTypes();
		Object[] methodParameters = new Object[methodParameterClazzs.length];
		
		for (int i = 0; i < methodParameterClazzs.length; i++) {
			
			if (methodParameterClazzs[i] == HttpServletRequest.class) {
				methodParameters[i] = request;
			} else if (methodParameterClazzs[i] == HttpServletResponse.class) {
				methodParameters[i] = response;
			} else {
				try {
					// TODO 与getBean比较
					methodParameters[i] = BeanUtil.instantiateBean(methodParameterClazzs[i]);
					Field[] methodParameterFields = methodParameterClazzs[i].getDeclaredFields();
					
					for (Field field : methodParameterFields) {
						Object value = requestParameters.get(field.getName());
						
						if (null != value) {
							field.setAccessible(true);
							// TODO
							field.set(methodParameters[i], value);;
						}
					}
				} catch (InstantiationException | IllegalAccessException e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		}
		Object result = null;

		try {
			result = method.invoke(webApplicationContext.getBean(method.getDeclaringClass()), 
					methodParameters);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error(e);
			e.printStackTrace();
		}
		response.setContentType("application/json");
		String jsonResult = JSON.toJSONString(result);
		response.getWriter().write(jsonResult);
	}

}
