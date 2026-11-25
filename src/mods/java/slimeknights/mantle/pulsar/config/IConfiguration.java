package slimeknights.mantle.pulsar.config;

import javax.annotation.ParametersAreNonnullByDefault;
import slimeknights.mantle.pulsar.pulse.PulseMeta;

@ParametersAreNonnullByDefault
public interface IConfiguration {
   void load();

   boolean isModuleEnabled(PulseMeta var1);

   void flush();
}
