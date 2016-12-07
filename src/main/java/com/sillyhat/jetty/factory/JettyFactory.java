package com.sillyhat.jetty.factory;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import com.google.common.collect.Lists;

public class JettyFactory {

	private static Logger logger = Logger.getLogger(JettyFactory.class);

	private static JettyFactory instance;

	private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";//默认webapp路径，当不使用maven 的module模式时，可使用默认路径

	private static final String WINDOWS_WEBDEFAULT_PATH = "jetty/webdefault.xml";//jetty配置文件路径

	private static final String REALM_PATH = "src/main/resources/jetty/realm.properties";//测试用户配置文件（暂时不需要）

	private JettyFactory() {
		
	}

//	public JettyFactory getInstance(){
//		logger.info("init factroy start");
//		if (null == instance){
//			synchronized (JettyFactory.class){
//				if (null == instance){
//					instance = new JettyFactory();
//				}
//			}
//		}
//		logger.info("init factroy end");
//		return instance;
//	}

	/**
	 * 暂时觉得只要简单单例就好
	 * @return
	 */
	public static JettyFactory getInstance(){
		logger.info("init factroy start");
		if (null == instance){
			instance = new JettyFactory();
		}
		logger.info("init factroy end");
		return instance;
	}

	/**
	 * 得到Jetty Server工厂，webapp将默认src/main/webapp目录
	 * @param port				端口号
	 * @param contextPath		项目名称
	 * @return
	 */
	public Server createServerInSource(int port, String contextPath) {
		return createServerInSource(port, contextPath,DEFAULT_WEBAPP_PATH);
	}

	/**
	 *得到Jetty Server工厂
	 * @param port				端口号
	 * @param contextPath		项目名称
	 * @param webapp			webapp目录
	 * @return
	 */
	public Server createServerInSource(int port, String contextPath,String webapp) {
		Server server = new Server();
		/*
			设置在JVM退出时关闭Jetty的钩子
			这样就可以在整个功能测试时启动一次Jetty,然后让它在JVM退出时自动关闭
		 */
		server.setStopAtShutdown(true);
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);//设置端口号
		/*
			解决Windows下重复启动Jetty不报告端口冲突的问题
			在Windows下有个Windows + Sun的connector实现的问题,reuseAddress=true时重复启动同一个端口的Jetty不会报错
			所以必须设为false,代价是若上次退出不干净(比如有TIME_WAIT),会导致新的Jetty不能启动,但权衡之下还是应该设为False
		 */
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[] { connector });
		WebAppContext webContext = new WebAppContext(webapp,contextPath);
//		SecurityHandler securityHandler = new ConstraintSecurityHandler();
//		HashLoginService hashLoginService = new HashLoginService();
//		hashLoginService.setConfig(REALM_PATH);
//		hashLoginService.setName("MyRealm");
//		securityHandler.setLoginService(hashLoginService);
//		webContext.setSecurityHandler(securityHandler);
		webContext.setMaxFormKeys(-1);//-1不限定表单key
		System.out.println("---------MaxFormKeys--------:" + webContext.getMaxFormKeys());
		System.out.println("---------MaxFormContentSize--------:" + webContext.getMaxFormContentSize());
		webContext.setDefaultsDescriptor(WINDOWS_WEBDEFAULT_PATH);
		server.setHandler(webContext);
		return server;
	}

//	public void setTldJarNames(Server server, String... jarNames) {
//		WebAppContext context = (WebAppContext) server.getHandler();
//		List<String> jarNameExprssions = Lists.newArrayList(new String[] {".*/jstl-[^/]*\\.jar$", ".*/.*taglibs[^/]*\\.jar$" });
//		for (String jarName : jarNames) {
//			jarNameExprssions.add(".*/" + jarName + "-[^/]*\\.jar$");
//		}
//		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", StringUtils.join(jarNameExprssions, '|'));
//	}

	public void reloadContext(Server server) throws Exception {
		WebAppContext context = (WebAppContext) server.getHandler();
		System.out.println("Application reloading");
		context.stop();
		WebAppClassLoader classLoader = new WebAppClassLoader(context);
		classLoader.addClassPath("target/classes");
		classLoader.addClassPath("target/test-classes");
		context.setClassLoader(classLoader);
		context.start();
		System.out.println("Application reloaded");
	}
}
