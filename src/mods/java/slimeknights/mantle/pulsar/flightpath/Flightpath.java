package slimeknights.mantle.pulsar.flightpath;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import slimeknights.mantle.pulsar.flightpath.lib.BlackholeExceptionHandler;
import slimeknights.mantle.pulsar.pulse.IPulse;

@ParametersAreNonnullByDefault
public class Flightpath {
   private final List<IPulse> subscribers = new ArrayList();
   private final Object lock = new Object();
   private IExceptionHandler exceptionHandler = new BlackholeExceptionHandler();

   public Flightpath() {
   }

   public void setExceptionHandler(IExceptionHandler handler) {
      synchronized(this.lock) {
         this.exceptionHandler = handler;
      }
   }

   public void register(IPulse obj) {
      synchronized(this.lock) {
         if (!this.subscribers.contains(obj)) {
            this.subscribers.add(obj);
         }
      }
   }

   public void post(Object evt) {
      synchronized(this.lock) {
         for(int i = 0; i < this.subscribers.size(); ++i) {
            try {
               ((IPulse)this.subscribers.get(i)).handlePulseEvent(evt);
            } catch (Exception var5) {
               this.exceptionHandler.handle(var5);
            }
         }

         this.exceptionHandler.flush();
      }
   }
}
