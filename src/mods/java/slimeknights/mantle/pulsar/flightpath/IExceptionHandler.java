package slimeknights.mantle.pulsar.flightpath;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IExceptionHandler {
   void handle(Exception var1);

   void flush();
}
