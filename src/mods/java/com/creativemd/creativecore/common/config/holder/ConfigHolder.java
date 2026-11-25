package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigHolder<T extends ConfigKey> implements ICreativeConfigHolder {
   public final ConfigSynchronization synchronization;
   public final ICreativeConfigHolder parent;
   public final String[] path;
   protected PairList<String, T> fields = new PairList<String, T>();

   public ConfigHolder(ICreativeConfigHolder parent, String key, ConfigSynchronization synchronization) {
      this.parent = parent;
      this.path = (String[])ObjectArrays.concat(parent.path(), key);
      this.synchronization = synchronization;
   }

   ConfigHolder() {
      this.parent = null;
      this.path = new String[0];
      this.synchronization = ConfigSynchronization.UNIVERSAL;
   }

   public ConfigSynchronization synchronization() {
      return this.synchronization;
   }

   public ICreativeConfigHolder parent() {
      return this.parent;
   }

   public String[] path() {
      return this.path;
   }

   public ConfigKey getField(String key) {
      return (ConfigKey)this.fields.getValue(key);
   }

   public Collection<? extends ConfigKey> fields() {
      return this.fields.values();
   }

   public Collection<String> names() {
      return this.fields.keys();
   }

   public Object get(String key) {
      T field = this.fields.getValue(key);
      return field != null ? field.get() : null;
   }

   public boolean isEmpty(Side side) {
      for(int i = 0; i < this.fields.size(); ++i) {
         T field = ((Pair)this.fields.get(i)).value;
         if (field.get() instanceof ICreativeConfigHolder) {
            if (!((ICreativeConfigHolder)field.get()).isEmpty(side)) {
               return false;
            }
         } else if (field.is(side)) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmptyWithoutForce(Side side) {
      for(int i = 0; i < this.fields.size(); ++i) {
         T field = ((Pair)this.fields.get(i)).value;
         if (field.get() instanceof ICreativeConfigHolder) {
            if (!((ICreativeConfigHolder)field.get()).isEmptyWithoutForce(side)) {
               return false;
            }
         } else if (field.isWithoutForce(side)) {
            return false;
         }
      }

      return true;
   }

   public boolean isDefault(Side side) {
      for(int i = 0; i < this.fields.size(); ++i) {
         if (((ConfigKey)((Pair)this.fields.get(i)).value).is(side) && !((ConfigKey)((Pair)this.fields.get(i)).value).isDefault(side)) {
            return false;
         }
      }

      return true;
   }

   public void restoreDefault(Side side, boolean ignoreRestart) {
      for(int i = 0; i < this.fields.size(); ++i) {
         T key = ((Pair)this.fields.get(i)).value;
         if (key.is(side) && (!ignoreRestart || !key.requiresRestart) && (!(key.get() instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder)key.get()).isEmpty(side))) {
            key.restoreDefault(side, ignoreRestart);
         }
      }

   }

   public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {
      for(int i = 0; i < this.fields.size(); ++i) {
         T field = ((Pair)this.fields.get(i)).value;
         if (field.is(side) && (!ignoreRestart || !field.requiresRestart)) {
            if (json.has(field.name)) {
               field.set(ConfigTypeConveration.read(field.getType(), field.getDefault(), loadDefault, ignoreRestart, json.get(field.name), side, field instanceof ConfigKey.ConfigKeyField ? (ConfigKey.ConfigKeyField)field : null));
            } else if (loadDefault && (!(field.get() instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder)field.get()).isEmpty(side))) {
               field.restoreDefault(side, ignoreRestart);
            }
         }
      }

   }

   public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side) {
      JsonObject object = new JsonObject();

      for(int i = 0; i < this.fields.size(); ++i) {
         T field = ((Pair)this.fields.get(i)).value;
         if (field.is(side) && (!ignoreRestart || !field.requiresRestart) && (saveDefault || !field.isDefault(side))) {
            object.add(field.name, ConfigTypeConveration.write(field.getType(), field.get(), field.getDefault(), saveDefault, ignoreRestart, side, field instanceof ConfigKey.ConfigKeyField ? (ConfigKey.ConfigKeyField)field : null));
         }
      }

      return object;
   }
}
