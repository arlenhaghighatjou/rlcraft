package com.charles445.simpledifficulty.api.config;

import net.minecraft.nbt.NBTTagCompound;

public class ServerConfig extends ConfigBase {
   public static ServerConfig instance = new ServerConfig();

   public ServerConfig() {
   }

   public void updateValues(NBTTagCompound compound) {
      for(String key : compound.getKeySet()) {
         if (this.values.containsKey(key)) {
            String newValue = compound.getString(key);
            this.values.put(key, newValue);
         }
      }

      QuickConfig.updateValues();
   }
}
