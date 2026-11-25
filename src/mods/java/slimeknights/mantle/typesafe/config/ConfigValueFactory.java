package slimeknights.mantle.typesafe.config;

import java.util.Map;
import slimeknights.mantle.typesafe.config.impl.ConfigImpl;

public final class ConfigValueFactory {
   private ConfigValueFactory() {
   }

   public static ConfigValue fromAnyRef(Object object, String originDescription) {
      return ConfigImpl.fromAnyRef(object, originDescription);
   }

   public static ConfigObject fromMap(Map<String, ? extends Object> values, String originDescription) {
      return (ConfigObject)fromAnyRef(values, originDescription);
   }

   public static ConfigList fromIterable(Iterable<? extends Object> values, String originDescription) {
      return (ConfigList)fromAnyRef(values, originDescription);
   }

   public static ConfigValue fromAnyRef(Object object) {
      return fromAnyRef(object, (String)null);
   }

   public static ConfigObject fromMap(Map<String, ? extends Object> values) {
      return fromMap(values, (String)null);
   }

   public static ConfigList fromIterable(Iterable<? extends Object> values) {
      return fromIterable(values, (String)null);
   }
}
