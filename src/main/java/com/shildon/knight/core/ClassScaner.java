package com.shildon.knight.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 加载扫描目录下的所有Class。
 * @author shildon<shildondu@gmail.com>
 * @date Jan 2, 2016 4:02:25 PM
 *
 */
public class ClassScaner {
	
	private static final String DEFAULT_PROPERTIES = "scan.properties";
	private static final String PACKAGE_NAME = "packageName";
	private static final String PROPERTIES_SUFFIX = ".properties";
	private static final String CLASS_SUFFIX = ".class";
	
	private static final String PROJECT_ROOT;
	static {
		// 获取项目根目录
		PROJECT_ROOT = Thread.currentThread().getContextClassLoader().getResource("./").getPath();
	}
	
	// 存储指定包名的全限定包名
	private static Map<String, List<String>> specifiedPackages = new HashMap<String, List<String>>();
	static {
		// 定时任务
		specifiedPackages.put(SpecifiedPackage.SCHEDULE, new LinkedList<String>());
		// AOP拦截
		specifiedPackages.put(SpecifiedPackage.INTERCEPTOR, new LinkedList<String>());
		// 控制器
		specifiedPackages.put(SpecifiedPackage.CONTROLLER, new LinkedList<String>());
	}
	
	// 通过标志域判断是否需要初始化指定的包名
	private static boolean flag = false;
	
	private static final Log log = LogFactory.getLog(ClassScaner.class);
	
	/*
	 * 加入指定包名
	 */
	private static void addSpecifiedPackage(String packageName, String fileName) {
		if (specifiedPackages.containsKey(fileName)) {
			specifiedPackages.get(fileName).add(packageName);
			
			if (log.isDebugEnabled()) {
				log.debug(fileName + " -> " + packageName);
			}
		}
	}
	
	public static List<Class<?>> loadClassBySpecify(String specifiedName) {
		List<Class<?>> clazzs = new LinkedList<Class<?>>();
		if (specifiedPackages.containsKey(specifiedName)) {
			List<String> packageNames = specifiedPackages.get(specifiedName);
			
			for (String packageName : packageNames) {
				String packagePath = packageName.replace(".", "/");
				File file = new File(PROJECT_ROOT + packagePath);

				if (log.isDebugEnabled()) {
					log.debug("packageName: " + packageName);
					log.debug("packagePath: " + packagePath);
				}

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
		List<Class<?>> clazzs = new LinkedList<Class<?>>();
		String packageName = getPackageName(DEFAULT_PROPERTIES);
		String packagePath = (null == packageName) ?
				"./" : packageName.replace(".", "/");
		URL url = Thread.currentThread().getContextClassLoader().
				getResource(packagePath);
		// 获取该url的协议，主要处理file和jar
		String protocol = url.getProtocol();
		
		if (log.isDebugEnabled()) {
			log.debug("The packageName: " + packageName);
			log.debug("The url: " + url.getPath());
			log.debug("The protocol: " + protocol);
		}
		
		try {
			if ("file".equals(protocol)) {
				File file = new File(url.toURI());
				addSpecifiedPackage(packageName, file.getName());
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
			log.error(e);
			e.printStackTrace();
		}
		flag = false;
		return clazzs;
	}
	
	/*
	 * 加载文件夹中的类文件
	 */
	private static void loadFileClass(String packageName, File file, List<Class<?>> clazzs) {
		
		if (log.isDebugEnabled()) {
			log.debug("in loadFileClass the packageName: " + packageName);
			log.debug("in loadFileClass the fileName: " + file.getName());
		}
		// 如果是目录，递归调用
		if (file.isDirectory()) {

			if (flag) {
				addSpecifiedPackage(packageName, file.getName());
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
				
				Class<?> clazz = null;
				try {
					clazz = Class.forName(fileName);
					clazzs.add(clazz);
				} catch (ClassNotFoundException e) {
					log.error(e);
					e.printStackTrace();
				}
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
				Class<?> clazz = null;
				try {
					clazz = Class.forName(jarName);
					clazzs.add(clazz);
				} catch (ClassNotFoundException e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 获取扫描路径。
	 */
	private static String getPackageName(String fileName) {
		Properties properties = new Properties();
		InputStream is = null;
		String packageName = null;
		
		// 构造绝对路径
		String path = PROJECT_ROOT + fileName;

		// 若配置文件不存在，则默认从项目根目录开始扫描
		if (!Files.exists(Paths.get(path))) {

		} else {
			if (null != fileName && fileName.endsWith(PROPERTIES_SUFFIX)) {
				is = Thread.currentThread().getContextClassLoader().
						getResourceAsStream(fileName);

				if (null != is) {
					try {
						properties.load(is);
						packageName = ((String) properties.get(PACKAGE_NAME));
						
						if (log.isDebugEnabled()) {
							log.debug(packageName);
						}

					} catch (IOException e) {
						log.error("Can not load the path!", e);
					}
				}
			}
		}
		
		return packageName;
	}
	
}
