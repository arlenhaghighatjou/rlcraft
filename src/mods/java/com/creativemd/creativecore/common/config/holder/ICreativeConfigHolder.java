package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraftforge.fml.relauncher.Side;

public interface ICreativeConfigHolder {
   ICreativeConfigHolder parent();

   default String getName() {
      return this.path()[this.path().length - 1];
   }

   String[] path();

   Collection<? extends ConfigKey> fields();

   Collection<String> names();

   Object get(String var1);

   ConfigKey getField(String var1);

   boolean isDefault(Side var1);

   void restoreDefault(Side var1, boolean var2);

   void load(boolean var1, boolean var2, JsonObject var3, Side var4);

   JsonObject save(boolean var1, boolean var2, Side var3);

   boolean isEmpty(Side var1);

   boolean isEmptyWithoutForce(Side var1);

   ConfigSynchronization synchronization();
}
