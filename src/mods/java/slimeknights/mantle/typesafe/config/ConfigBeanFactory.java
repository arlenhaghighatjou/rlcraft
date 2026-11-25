package slimeknights.mantle.typesafe.config;

import slimeknights.mantle.typesafe.config.impl.ConfigBeanImpl;

public class ConfigBeanFactory {
   public ConfigBeanFactory() {
   }

   public static <T> T create(Config config, Class<T> clazz) {
      return (T)ConfigBeanImpl.createInternal(config, clazz);
   }
}
