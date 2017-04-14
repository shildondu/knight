package com.shildon.knight.mvc;

import com.shildon.knight.core.support.WebApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 加载应用上下文。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 3:02:16 PM
 *
 */
public class ContextLoader implements ServletContextListener {
	
	public static final String WEB_ROOT = "WEB_APPLICATION_CONTEXT";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextLoader.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * 初始化应用上下文环境
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// 获取Servlet上下文
		ServletContext servletContext = servletContextEvent.getServletContext();
		WebApplicationContext applicationContext = 
				new WebApplicationContext(servletContext);
		servletContext.setAttribute(WEB_ROOT, applicationContext);

		LOGGER.debug("load web application context successfully!");
	}

}
