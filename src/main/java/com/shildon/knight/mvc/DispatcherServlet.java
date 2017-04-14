package com.shildon.knight.mvc;

import com.alibaba.fastjson.JSON;
import com.shildon.knight.core.ApplicationContext;
import com.shildon.knight.core.ClassScanner;
import com.shildon.knight.core.SpecifiedPackage;
import com.shildon.knight.core.support.WebApplicationContext;
import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.mvc.annotation.RequestMapping;
import com.shildon.knight.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求分发器。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 3:31:57 PM
 *
 */
public class DispatcherServlet extends HttpServlet {

	private ApplicationContext webApplicationContext;
	private Map<String, Method> requestMap;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 获取Web应用上下文
		webApplicationContext = (WebApplicationContext) config.getServletContext()
				.getAttribute(ContextLoader.WEB_ROOT);
		// 只写一次，所以不考虑并发问题
		requestMap = new HashMap<>();
		// 初始化requestMap
		Map<String, Class<?>> clazzs = ReflectUtil.
				getAnnotationClazzs(ClassScanner.loadClassBySpecify(SpecifiedPackage.CONTROLLER.getPackageName()),
						Bean.class);
		
		for (Class<?> clazz : clazzs.values()) {
			List<Method> handlerMethods = ReflectUtil.getAnnotationMethods(clazz, RequestMapping.class);
			
			for (Method method : handlerMethods) {
				Annotation annotation = method.getAnnotation(RequestMapping.class);
				String uri = "";

				try {
					uri = (String) annotation.annotationType().getMethod("value").invoke(annotation);
					requestMap.put(uri, method);

					LOGGER.debug("{} -> {}", method.getName(), uri);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException e) {
				    LOGGER.error("get requeset map error! uri: {}", uri);
				}
			}
		}

		LOGGER.debug("init dispatcher servlet successfully!");
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		super.service(request, response);
		// 获取请求uri
		String requestUri = request.getRequestURI().
				substring(request.getContextPath().length());
		// TODO
		Map<String, String[]> requestParameters = request.getParameterMap();
		Method method = requestMap.get(requestUri);
		
		if (null == method) {
			response.getWriter().write("<h1>404 Not Found!</h1>");
		} else {
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
						methodParameters[i] = ReflectUtil.instantiateBean(methodParameterClazzs[i]);
						Field[] methodParameterFields = methodParameterClazzs[i].getDeclaredFields();

						for (Field field : methodParameterFields) {
							Object value = requestParameters.get(field.getName());

							if (null != value) {
								field.setAccessible(true);
								// TODO
								field.set(methodParameters[i], value);
							}
						}
					} catch (InstantiationException | IllegalAccessException |
                            NoSuchMethodException | InvocationTargetException e) {
						LOGGER.error("instantiate bean: {} fail!", methodParameterClazzs[i].getName());
					}
				}
			}
			Object result = null;

			try {
				result = method.invoke(webApplicationContext.getBean(method.getDeclaringClass()),
						methodParameters);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
			    LOGGER.error("invoke method: {} fail!", method.getName());
			}
			response.setContentType("application/json");
			String jsonResult = JSON.toJSONString(result);
			response.getWriter().write(jsonResult);
		}
	}

}
