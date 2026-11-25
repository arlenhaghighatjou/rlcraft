package org.spongepowered.asm.service;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterConsole;

public final class MixinService {
   private static MixinService.LogBuffer logBuffer = new MixinService.LogBuffer();
   private static MixinService instance;
   private ServiceLoader<IMixinServiceBootstrap> bootstrapServiceLoader;
   private final Set<String> bootedServices = new HashSet();
   private ServiceLoader<IMixinService> serviceLoader;
   private IMixinService service = null;
   private IGlobalPropertyService propertyService;

   private MixinService() {
      this.runBootServices();
   }

   private void runBootServices() {
      this.bootstrapServiceLoader = ServiceLoader.load(IMixinServiceBootstrap.class, this.getClass().getClassLoader());

      for(IMixinServiceBootstrap bootService : this.bootstrapServiceLoader) {
         try {
            bootService.bootstrap();
            this.bootedServices.add(bootService.getServiceClassName());
         } catch (ServiceInitialisationException var3) {
            logBuffer.debug("Mixin bootstrap service {} is not available: {}", var3.getStackTrace()[0].getClassName(), var3.getMessage());
         } catch (Throwable var4) {
            logBuffer.debug("Catching {}:{} initialising service", var4.getClass().getName(), var4.getMessage(), var4);
         }
      }

   }

   private static MixinService getInstance() {
      if (instance == null) {
         instance = new MixinService();
      }

      return instance;
   }

   public static void boot() {
      getInstance();
   }

   public static IMixinService getService() {
      return getInstance().getServiceInstance();
   }

   private synchronized IMixinService getServiceInstance() {
      if (this.service == null) {
         try {
            this.service = this.initService();
            ILogger serviceLogger = this.service.getLogger("mixin");
            logBuffer.flush(serviceLogger);
         } catch (Error var3) {
            ILogger defaultLogger = (ILogger)getDefaultLogger();
            logBuffer.flush(defaultLogger);
            defaultLogger.error(var3.getMessage(), var3);
            throw var3;
         }
      }

      return this.service;
   }

   private IMixinService initService() {
      this.serviceLoader = ServiceLoader.load(IMixinService.class, this.getClass().getClassLoader());
      Iterator<IMixinService> iter = this.serviceLoader.iterator();
      List<String> badServices = new ArrayList();
      int brokenServiceCount = 0;

      while(iter.hasNext()) {
         try {
            IMixinService service = (IMixinService)iter.next();
            if (this.bootedServices.contains(service.getClass().getName())) {
               logBuffer.debug("MixinService [{}] was successfully booted in {}", service.getName(), this.getClass().getClassLoader());
            }

            if (service.isValid()) {
               return service;
            }

            logBuffer.debug("MixinService [{}] is not valid", service.getName());
            badServices.add(String.format("INVALID[%s]", service.getName()));
         } catch (ServiceConfigurationError var7) {
            ++brokenServiceCount;
         } catch (Throwable var8) {
            String faultingClassName = var8.getStackTrace()[0].getClassName();
            logBuffer.debug("MixinService [{}] failed initialisation: {}", faultingClassName, var8.getMessage());
            int pos = faultingClassName.lastIndexOf(46);
            badServices.add(String.format("ERROR[%s]", pos < 0 ? faultingClassName : faultingClassName.substring(pos + 1)));
         }
      }

      String brokenServiceNote = brokenServiceCount == 0 ? "" : " and " + brokenServiceCount + " other invalid services.";
      throw new ServiceNotAvailableError("No mixin host service is available. Services: " + Joiner.on(", ").join(badServices) + brokenServiceNote);
   }

   public static IGlobalPropertyService getGlobalPropertyService() {
      return getInstance().getGlobalPropertyServiceInstance();
   }

   private IGlobalPropertyService getGlobalPropertyServiceInstance() {
      if (this.propertyService == null) {
         this.propertyService = this.initPropertyService();
      }

      return this.propertyService;
   }

   private IGlobalPropertyService initPropertyService() {
      for(IGlobalPropertyService service : ServiceLoader.load(IGlobalPropertyService.class, this.getClass().getClassLoader())) {
         try {
            return service;
         } catch (ServiceConfigurationError var4) {
            ;
         } catch (Throwable var5) {
            ;
         }
      }

      throw new ServiceNotAvailableError("No mixin global property service is available");
   }

   private static <T> T getDefaultLogger() {
      return (T)(new LoggerAdapterConsole("mixin")).setDebugStream(System.err);
   }

   static class LogBuffer {
      private final List<MixinService.LogBuffer.LogEntry> buffer = new ArrayList();
      private ILogger logger;

      LogBuffer() {
      }

      synchronized void debug(String message, Object... params) {
         if (this.logger != null) {
            this.logger.debug(message, params);
         } else {
            this.buffer.add(new MixinService.LogBuffer.LogEntry(message, params, (Throwable)null));
         }
      }

      synchronized void debug(String message, Throwable t) {
         if (this.logger != null) {
            this.logger.debug(message, t);
         } else {
            this.buffer.add(new MixinService.LogBuffer.LogEntry(message, new Object[0], t));
         }
      }

      synchronized void flush(ILogger logger) {
         for(MixinService.LogBuffer.LogEntry buffered : this.buffer) {
            if (buffered.t != null) {
               logger.debug(buffered.message, ObjectArrays.concat(buffered.params, buffered.t));
            } else {
               logger.debug(buffered.message, buffered.params);
            }
         }

         this.buffer.clear();
         this.logger = logger;
      }

      public static class LogEntry {
         public String message;
         public Object[] params;
         public Throwable t;

         public LogEntry(String message, Object[] params, Throwable t) {
            this.message = message;
            this.params = params;
            this.t = t;
         }
      }
   }
}
