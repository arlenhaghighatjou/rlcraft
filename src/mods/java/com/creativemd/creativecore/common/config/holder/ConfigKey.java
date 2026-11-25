package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import java.lang.reflect.Field;
import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigKey {
   public final String name;
   public final ConfigSynchronization synchronization;
   public final boolean requiresRestart;
   protected final Object defaultValue;
   public boolean forceSynchronization;

   public ConfigKey(String fieldName, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
      this.synchronization = synchronization;
      this.requiresRestart = requiresRestart;
      if (name.isEmpty()) {
         this.name = fieldName;
      } else {
         this.name = name;
      }

      this.defaultValue = defaultValue;
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof ConfigKey ? ((ConfigKey)obj).name.equals(this.name) : false;
   }

   public String toString() {
      return this.name;
   }

   public abstract void set(Object var1);

   public abstract Object get();

   protected boolean checkEqual(Object one, Object two) {
      return one.equals(two);
   }

   public boolean isDefault(Side side) {
      return this.defaultValue instanceof ICreativeConfigHolder ? ((ICreativeConfigHolder)this.defaultValue).isDefault(side) : this.checkEqual(this.defaultValue, this.get());
   }

   public boolean isDefault(Object value, Side side) {
      return this.defaultValue instanceof ICreativeConfigHolder ? ((ICreativeConfigHolder)this.defaultValue).isDefault(side) : this.checkEqual(this.defaultValue, value);
   }

   public void restoreDefault(Side side, boolean ignoreRestart) {
      if (this.defaultValue instanceof ICreativeConfigHolder) {
         ((ICreativeConfigHolder)this.defaultValue).restoreDefault(side, ignoreRestart);
      } else {
         this.set(this.defaultValue);
      }

   }

   public Object getDefault() {
      return this.defaultValue;
   }

   public Class getType() {
      return this.defaultValue.getClass();
   }

   public boolean is(Side side) {
      return this.defaultValue instanceof ICreativeConfigHolder ? this.synchronization.useFolder(this.forceSynchronization, side) : this.synchronization.useValue(this.forceSynchronization, side);
   }

   public boolean isWithoutForce(Side side) {
      return this.defaultValue instanceof ICreativeConfigHolder ? this.synchronization.useFolder(false, side) : this.synchronization.useValue(false, side);
   }

   public static class ConfigKeyDynamic extends ConfigKey {
      private Object value;

      public ConfigKeyDynamic(String name, Object defaultValue, ConfigSynchronization type, boolean requiresRestart) {
         super(name, "", defaultValue, type, requiresRestart);
         if (defaultValue instanceof ICreativeConfigHolder) {
            this.value = null;
         } else {
            this.value = defaultValue;
         }

      }

      public void set(Object object) {
         if (!(this.defaultValue instanceof ICreativeConfigHolder)) {
            this.value = object;
         }

      }

      public Object get() {
         return this.defaultValue instanceof ICreativeConfigHolder ? this.defaultValue : this.value;
      }
   }

   public static class ConfigKeyDynamicField extends ConfigKey.ConfigKeyField {
      public final Object parent;

      public ConfigKeyDynamicField(Field field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart, Object parent) {
         super(field, name, defaultValue, synchronization, requiresRestart);
         this.parent = parent;
      }

      public Object getParent() {
         return this.parent;
      }
   }

   public abstract static class ConfigKeyField extends ConfigKey {
      public final Field field;
      public final ConfigTypeConveration converation;

      public ConfigKeyField(Field field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
         super(name, name, defaultValue, synchronization, requiresRestart);
         this.field = field;
         if (defaultValue instanceof ICreativeConfigHolder) {
            this.converation = null;
         } else {
            this.converation = ConfigTypeConveration.get(field.getType());
         }

      }

      public abstract Object getParent();

      public void set(Object object) {
         try {
            if (!(this.defaultValue instanceof ICreativeConfigHolder)) {
               this.field.set(this.getParent(), this.converation.set(this, object));
            }

         } catch (IllegalAccessException | IllegalArgumentException var3) {
            throw new RuntimeException(var3);
         }
      }

      public Object get() {
         try {
            return this.defaultValue instanceof ICreativeConfigHolder ? this.defaultValue : this.field.get(this.getParent());
         } catch (IllegalAccessException | IllegalArgumentException var2) {
            throw new RuntimeException(var2);
         }
      }

      protected boolean checkEqual(Object one, Object two) {
         return this.converation != null ? this.converation.areEqual(one, two, this) : super.checkEqual(one, two);
      }
   }
}
