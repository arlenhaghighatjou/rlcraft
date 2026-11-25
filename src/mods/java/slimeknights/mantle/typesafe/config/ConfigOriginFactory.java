package slimeknights.mantle.typesafe.config;

import java.net.URL;
import slimeknights.mantle.typesafe.config.impl.ConfigImpl;

public final class ConfigOriginFactory {
   private ConfigOriginFactory() {
   }

   public static ConfigOrigin newSimple() {
      return newSimple((String)null);
   }

   public static ConfigOrigin newSimple(String description) {
      return ConfigImpl.newSimpleOrigin(description);
   }

   public static ConfigOrigin newFile(String filename) {
      return ConfigImpl.newFileOrigin(filename);
   }

   public static ConfigOrigin newURL(URL url) {
      return ConfigImpl.newURLOrigin(url);
   }
}
