package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import net.minecraftforge.fml.relauncher.Side;

public class CreativeConfigRegistry extends ConfigHolderDynamic {
   public static final CreativeConfigRegistry ROOT = new CreativeConfigRegistry();

   public CreativeConfigRegistry() {
   }

   public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
      return this.registerValue(key, defaultValue, ConfigSynchronization.UNIVERSAL, false);
   }

   public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
      return super.registerValue(key, defaultValue, synchronization, requiresRestart);
   }

   public ICreativeConfigHolder followPath(String[] path) {
      ICreativeConfigHolder current = this;

      for(int i = 0; i < path.length; ++i) {
         Object object = current.get(path[i]);
         if (!(object instanceof ICreativeConfigHolder)) {
            return null;
         }

         current = (ICreativeConfigHolder)object;
      }

      return this;
   }

   public ConfigKey findKey(String[] path) {
      ICreativeConfigHolder current = this;

      for(int i = 0; i < path.length; ++i) {
         if (i + 1 == path.length) {
            return current.getField(path[i]);
         }

         Object object = current.get(path[i]);
         if (!(object instanceof ICreativeConfigHolder)) {
            return null;
         }

         current = (ICreativeConfigHolder)object;
      }

      return null;
   }

   public boolean removeField(String modid) {
      return this.fields.removeKey(modid);
   }

   public static void load(String modid, Side side) {
      CreativeCore.configHandler.load(modid, side);
   }
}
