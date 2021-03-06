package com.shildon.knight.core;

import com.shildon.knight.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 加载扫描目录下的所有Class。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 2, 2016 4:02:25 PM
 *
 */
public class ClassScanner {
	
	private static final String DEFAULT_PROPERTIES = "scan.properties";
	private static final String PACKAGE_NAME = "packageName";
	private static final String PROPERTIES_SUFFIX = ".properties";
	private static final String CLASS_SUFFIX = ".class";
	
	private static final String PROJECT_ROOT;
	static {
		// 获取项目根目录classpath
		PROJECT_ROOT = Thread.currentThread().getContextClassLoader().getResource("./").getPath();
	}
	
	// 存储指定包名的全限定包名
	private static Map<String, List<String>> specifiedPackages = new HashMap<>();
	static {
		// 定时任务
		specifiedPackages.put(SpecifiedPackage.SCHEDULE.getPackageName(), new LinkedList<>());
		// AOP拦截
		specifiedPackages.put(SpecifiedPackage.INTERCEPTOR.getPackageName(), new LinkedList<>());
		// 控制器
		specifiedPackages.put(SpecifiedPackage.CONTROLLER.getPackageName(), new LinkedList<>());
	}
	
	// 通过标志域判断是否需要初始化指定的包名
	private static boolean flag = false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);
	
	/**
	 * 加入指定包名
	 * @param fileName 文件名
	 * @param packageName 全限定包名
	 */
	private static void addSpecifiedPackage(String fileName, String packageName) {
		if (specifiedPackages.containsKey(fileName)) {
			specifiedPackages.get(fileName).add(packageName);

			LOGGER.debug("{} -> {}", fileName, packageName);
		}
	}
	
	public static List<Class<?>> loadClassBySpecify(String specifiedName) {
		List<Class<?>> clazzs = new LinkedList<>();
		if (specifiedPackages.containsKey(specifiedName)) {
			List<String> packageNames = specifiedPackages.get(specifiedName);
			
			for (String packageName : packageNames) {
				String packagePath = packageName.replace(".", "/");
				File file = new File(PROJECT_ROOT + packagePath);

                LOGGER.debug("packageName: {}", packageName);
                LOGGER.debug("packagePath: {}", packagePath);

				loadFileClass(packageName, file, clazzs);
			}
		}
		return clazzs;
	}
	
	/**
	 * 加载指定路径下的所有类
	 * @return
	 */
	public static List<Class<?>> loadClass() {
		flag = true;
		List<Class<?>> clazzs = new LinkedList<>();
		String packageName = getPackageName(DEFAULT_PROPERTIES);
		String packagePath = (null == packageName) ?
				"./" : packageName.replace(".", "/");
		URL url = Thread.currentThread().getContextClassLoader().
				getResource(packagePath);
		// 获取该url的协议，主要处理file和jar
		String protocol = url.getProtocol();

        LOGGER.debug("The packageName: {}", packageName);
        LOGGER.debug("The url: {}", url.getPath());
        LOGGER.debug("The protocol: {}", protocol);

		try {
			if ("file".equals(protocol)) {
				File file = new File(url.toURI());
				addSpecifiedPackage(file.getName(), packageName);
				File[] files = file.listFiles();
				
				for (File f : files) {
					// 拼接字符串
					String newPackageName = (null == packageName) ? f.getName() :
						packageName + "." + f.getName();
					loadFileClass(newPackageName, f, clazzs);
				}
			} else if ("jar".equals(protocol)) {
				JarFile jarFile = ((JarURLConnection) url.openConnection()).
						getJarFile();
				loadJarClass(jarFile, clazzs);
			}
		} catch (URISyntaxException | IOException e) {
		    LOGGER.error("get jar file error!");
		}
		flag = false;
		return clazzs;
	}
	
	/*
	 * 加载文件夹中的类文件
	 */
	private static void loadFileClass(String packageName, File file, List<Class<?>> clazzs) {

        LOGGER.debug("in loadFileClass the packageName: {}", packageName);
        LOGGER.debug("in loadFileClass the fileName: {}", file.getName());
		// 如果是目录，递归调用
		if (file.isDirectory()) {

			if (flag) {
				addSpecifiedPackage(file.getName(), packageName);
			}
			File[] files = file.listFiles();
			
			for (File f : files) {
				String newPackageName = (null == packageName) ? f.getName() :
					packageName + "." + f.getName();
				loadFileClass(newPackageName, f, clazzs);
			}
		} else {
			if (packageName.endsWith(CLASS_SUFFIX)) {
				// 去除.class后缀
				int fileNameLength = packageName.lastIndexOf(CLASS_SUFFIX);
				String fileName = packageName.substring(0, fileNameLength);
				add2Clazzs(fileName, clazzs);
			}
		}
	}
	
	/*
	 * 加载jar文件中的类文件
	 */
	private static void loadJarClass(JarFile jarFile, List<Class<?>> clazzs) {
		Enumeration<JarEntry> jarEntires = jarFile.entries();
		while (jarEntires.hasMoreElements()) {
			JarEntry jarEntry = jarEntires.nextElement();
			String jarName = jarEntry.getName().replace("/", ".");
			if (jarName.endsWith(CLASS_SUFFIX)) {
				add2Clazzs(jarName, clazzs);
			}
		}
	}

	private static void add2Clazzs(String name, List<Class<?>> clazzs) {
		try {
			Class<?> clazz = Class.forName(name);
			clazzs.add(clazz);
		} catch (ClassNotFoundException e) {
		    LOGGER.error("class not found: {}", name);
		}
	}
	
	/*
	 * 获取扫描路径。
	 */
	private static String getPackageName(String fileName) {
		PropertiesUtil propertiesUtil;
		InputStream is;
		String packageName = null;
		
		// 构造绝对路径
		String pathStr = PROJECT_ROOT + fileName;

		// 若配置文件不存在，则默认从项目根目录开始扫描
        File file = new File(pathStr);
		if (!file.exists()) {

		} else {
			if (null != fileName && fileName.endsWith(PROPERTIES_SUFFIX)) {
				is = Thread.currentThread().getContextClassLoader().
						getResourceAsStream(fileName);

				if (null != is) {
					propertiesUtil = new PropertiesUtil(is);
					packageName = ((String) propertiesUtil.getValue(PACKAGE_NAME));

					LOGGER.error("in method getPackageName() the packageName is: {}", packageName);
				}
			}
		}
		
		return packageName;
	}
	
}
