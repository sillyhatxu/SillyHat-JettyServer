#SillyHat-JettyServer   嘿咻傻帽-简单封装Jetty容器
##使用时只需复制如下代码

###注意：如使用默认webapp路径报错，则可手动配置webapp路径

    package start;
    
    import com.sillyhat.jetty.factory.JettyFactory;
    import org.eclipse.jetty.server.Server;
    
    public class StartServer {
    	public static final int PORT = 8802;
    	public static final String CONTEXT = "/SillyHatStudyLibrary";
    	public static final String BASE_URL = "http://localhost:"+PORT+CONTEXT;
    	public static final String webappPath = System.getProperty("user.dir") + "\\SillyHatStudyLibrary-appweb\\src\\main\\webapp";
    
    	public static void main(String[] args) throws Exception {
    		System.setProperty("spring.profiles.active", "development");
    		try {
    			JettyFactory jettyFactory = JettyFactory.getInstance();
    			//使用自定义webapp路径，适用于module方式maven项目
    			Server server = jettyFactory.createServerInSource(PORT, CONTEXT,webappPath);
    			//使用默认webapp路径，适用于普通maven项目
    //			Server server = jettyFactory.createServerInSource(PORT, CONTEXT);
    			server.start();// 启动Jetty
    			System.out.println("[INFO] Server running at http://localhost:" + PORT + CONTEXT);
    			System.out.println("Start End!");
                //运行cmd，启动成功后打开浏览器页面
    			Runtime.getRuntime().exec("cmd.exe /c start " + BASE_URL);
    			System.out.println("[HINT] Hit Enter to reload the application quickly");
    			// 等待用户输入回车重载应用.
    			while (true) {
    				char c = (char) System.in.read();
    				if (c == '\n') {
    					jettyFactory.reloadContext(server);
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.exit(-1);
    		}
    	}
    }


