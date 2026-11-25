package slimeknights.mantle.pulsar.flightpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.ParametersAreNonnullByDefault;
import slimeknights.mantle.pulsar.flightpath.lib.AnnotationLocator;
import slimeknights.mantle.pulsar.flightpath.lib.BlackholeExceptionHandler;

@ParametersAreNonnullByDefault
public class Flightpath {
   private final ISubscriberLocator locator;
   private final LinkedHashMap<Object, Map<Class, Set<Method>>> subscribers = new LinkedHashMap();
   private final Object lock = new Object();
   private IExceptionHandler exceptionHandler = new BlackholeExceptionHandler();

   public Flightpath() {
      this.locator = new AnnotationLocator(Airdrop.class);
   }

   public Flightpath(ISubscriberLocator locator) {
      this.locator = locator;
   }

   public void setExceptionHandler(IExceptionHandler handler) {
      synchronized(this.lock) {
         this.exceptionHandler = handler;
      }
   }

   public void register(Object obj) {
      synchronized(this.lock) {
         if (!this.subscribers.containsKey(obj)) {
            this.subscribers.put(obj, this.locator.findSubscribers(obj));
         }
      }
   }

   public void post(Object evt) {
      synchronized(this.lock) {
         for(Entry<Object, Map<Class, Set<Method>>> ent : this.subscribers.entrySet()) {
            for(Entry<Class, Set<Method>> objEnt : ((Map)ent.getValue()).entrySet()) {
               if (((Class)objEnt.getKey()).isAssignableFrom(evt.getClass())) {
                  for(Method m : (Set)objEnt.getValue()) {
                     try {
                        boolean access = m.isAccessible();
                        m.setAccessible(true);
                        m.invoke(ent.getKey(), evt);
                        m.setAccessible(access);
                     } catch (InvocationTargetException var12) {
                        if (var12.getTargetException() instanceof Exception) {
                           this.exceptionHandler.handle((Exception)var12.getTargetException());
                        } else {
                           this.exceptionHandler.handle(var12);
                        }
                     } catch (Exception var13) {
                        this.exceptionHandler.handle(var13);
                     }
                  }
               }
            }
         }

         this.exceptionHandler.flush();
      }
   }
}
