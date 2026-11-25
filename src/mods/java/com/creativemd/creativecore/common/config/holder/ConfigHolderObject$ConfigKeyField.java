package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import java.lang.reflect.Field;

public class ConfigHolderObject$ConfigKeyField extends ConfigKey {
   public final Field field;
   public final ConfigTypeConveration converation;

   public ConfigHolderObject$ConfigKeyField(ConfigHolderObject this$0, Field field, String configName, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
      super(field.getName(), configName, defaultValue, synchronization, requiresRestart);
      this.this$0 = this$0;
      this.field = field;
      if (defaultValue instanceof ICreativeConfigHolder) {
         this.converation = null;
      } else {
         this.converation = ConfigTypeConveration.get(field.getType());
      }

   }

   public void set(Object object) {
      try {
         if (!(this.defaultValue instanceof ICreativeConfigHolder)) {
            this.field.set(this.this$0.object, this.converation.set(this, object));
         }

      } catch (IllegalAccessException | IllegalArgumentException var3) {
         throw new RuntimeException(var3);
      }
   }

   public Object get() {
      try {
         return this.defaultValue instanceof ICreativeConfigHolder ? this.defaultValue : this.field.get(this.this$0.object);
      } catch (IllegalAccessException | IllegalArgumentException var2) {
         throw new RuntimeException(var2);
      }
   }

   protected boolean checkEqual(Object one, Object two) {
      return this.converation != null ? this.converation.areEqual(one, two) : super.checkEqual(one, two);
   }

   public ConfigHolderObject getHolder() {
      return this.this$0;
   }
}
