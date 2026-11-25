package slimeknights.mantle.pulsar.internal;

import javax.annotation.ParametersAreNonnullByDefault;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import slimeknights.mantle.pulsar.flightpath.IExceptionHandler;

@ParametersAreNonnullByDefault
public final class BusExceptionHandler implements IExceptionHandler {
   private final String id;
   private final Logger logger;

   public BusExceptionHandler(String id) {
      this.id = id;
      this.logger = LogManager.getLogger(id + "-Pulsar-Flightpath");
   }

   public void handle(Exception ex) {
      this.logger.error("Exception caught from a pulse on flightpath for mod ID " + this.id + ": ", ex);
      if (ex instanceof RuntimeException) {
         throw (RuntimeException)ex;
      } else {
         throw new Error(ex);
      }
   }

   public void flush() {
   }
}
