package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import java.lang.reflect.Field;

public class ConfigHolderDynamic extends ConfigHolder<ConfigKey> {
   public ConfigHolderDynamic(ICreativeConfigHolder parent, String key, ConfigSynchronization synchronization) {
      super(parent, key, synchronization);
   }

   ConfigHolderDynamic() {
   }

   public ConfigHolderDynamic registerFolder(String key) {
      return this.registerFolder(key, ConfigSynchronization.UNIVERSAL);
   }

   public ConfigHolderDynamic registerFolder(String key, ConfigSynchronization synchronization) {
      if (!key.contains(".") && !key.contains("/")) {
         if (this.fields.containsKey(key)) {
            throw new RuntimeException("Key already registered " + key + "");
         } else {
            synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
            ConfigHolderDynamic holder = new ConfigHolderDynamic(this, key, synchronization);
            this.fields.add(key, new ConfigKey.ConfigKeyDynamic(key, holder, synchronization, false));
            return holder;
         }
      } else {
         throw new RuntimeException("Invalid key " + key + "");
      }
   }

   public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
      return this.registerValue(key, defaultValue, ConfigSynchronization.UNIVERSAL, false);
   }

   public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
      if (ConfigTypeConveration.has(defaultValue.getClass())) {
         throw new IllegalArgumentException("Only holder objects are allowed");
      } else if (!key.contains(".") && !key.contains("/")) {
         if (this.fields.containsKey(key)) {
            throw new RuntimeException("Key already registered " + key + "");
         } else {
            synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
            this.fields.add(key, new ConfigKey.ConfigKeyDynamic(key, ConfigTypeConveration.parseObject(this, synchronization, key, defaultValue), synchronization, requiresRestart));
            return this;
         }
      } else {
         throw new RuntimeException("Invalid key " + key + "");
      }
   }

   public void registerField(String key, Field field, Object object) {
      this.registerField(key, field, object, ConfigSynchronization.CLIENT, false);
   }

   public void registerField(String key, Field field, Object object, ConfigSynchronization synchronization, boolean requiresRestart) {
      synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
      if (!ConfigTypeConveration.has(field.getType())) {
         throw new RuntimeException("Field cannot contain holder object, use register value instead");
      } else {
         try {
            ConfigKey.ConfigKeyDynamicField configKey = new ConfigKey.ConfigKeyDynamicField(field, key, field.get(object), synchronization, requiresRestart, object);
            this.fields.add(key, configKey);
         } catch (IllegalAccessException | IllegalArgumentException var7) {
            var7.printStackTrace();
         }

      }
   }
}
