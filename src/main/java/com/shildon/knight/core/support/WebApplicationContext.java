package com.shildon.knight.core.support;

import javax.servlet.ServletContext;

/**
 * Web应用上下文。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 16, 2016 3:03:20 PM
 *
 */
public class WebApplicationContext extends DefaultApplicationContext {
	
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
