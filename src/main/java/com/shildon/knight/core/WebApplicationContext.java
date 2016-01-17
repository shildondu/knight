package com.shildon.knight.core;

import javax.servlet.ServletContext;

import com.shildon.knight.ioc.support.DefaultBeanFactory;

/**
 * Web应用上下文。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 3:03:20 PM
 *
 */
public class WebApplicationContext extends DefaultBeanFactory {
	
	private ServletContext servletContext;
	
	public WebApplicationContext(ServletContext servletContext) {
		this.setServletContext(servletContext);
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
