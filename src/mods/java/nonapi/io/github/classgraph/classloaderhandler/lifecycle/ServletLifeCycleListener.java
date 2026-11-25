package nonapi.io.github.classgraph.classloaderhandler.lifecycle;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletLifeCycleListener implements ServletContextListener {
   private final Logger log = Logger.getLogger(ClassGraph.class.getName());

   public ServletLifeCycleListener() {
   }

   public void contextInitialized(ServletContextEvent event) {
      this.log.info("Servlet context initialized");
   }

   public void contextDestroyed(ServletContextEvent event) {
      this.log.info("Servlet context destroyed -- closing any remaning open ClassGraph ScanResult instances");
      ScanResult.closeAll();
   }
}
