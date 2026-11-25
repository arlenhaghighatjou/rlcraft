package nonapi.io.github.classgraph.classloaderhandler.lifecycle;

import javax.servlet.ServletContextListener;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringLifeCycleListener {
   public SpringLifeCycleListener() {
   }

   @Bean
   ServletListenerRegistrationBean<ServletContextListener> servletListener() {
      ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean();
      srb.setListener(new ServletLifeCycleListener());
      return srb;
   }
}
