package com.shildon.knight.core;

/**
 * 常量接口，指定具有指定意义的包名。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 14, 2016 4:35:40 PM
 *
 */
public enum SpecifiedPackage {

    // 定时任务，拦截器，控制器包名
	SCHEDULE("schedule"), INTERCEPTOR("interceptor"), CONTROLLER("controller");

	private String packageName;

	SpecifiedPackage(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return this.packageName;
	}

}
