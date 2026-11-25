package goblinbob.mobends.core.configuration;

import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.network.SharedProperty;
import java.io.File;

public class CoreServerConfig extends CoreConfig {
   private static final String CATEGORY_SERVER = "Server";

   public CoreServerConfig(File file) {
      super(file);

      for(SharedProperty<?> prop : NetworkConfiguration.instance.getSharedConfig().getProperties()) {
         prop.updateWithConfig(this.configuration, "Server");
      }

      this.configuration.save();
   }

   public void save() {
   }
}
