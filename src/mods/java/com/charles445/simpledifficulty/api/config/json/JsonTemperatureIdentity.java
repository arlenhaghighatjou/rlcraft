package com.charles445.simpledifficulty.api.config.json;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsonTemperatureIdentity {
   public JsonItemIdentity identity;
   public float temperature;

   public JsonTemperatureIdentity(float temperature, int metadata) {
      this(temperature, new JsonItemIdentity(metadata));
   }

   public JsonTemperatureIdentity(float temperature, int metadata, String nbt) {
      this(temperature, new JsonItemIdentity(metadata, nbt));
   }

   public JsonTemperatureIdentity(float temperature, JsonItemIdentity identity) {
      this.temperature = temperature;
      this.identity = identity;
   }

   public boolean matches(ItemStack stack) {
      return this.identity.matches(stack);
   }

   public boolean matches(JsonItemIdentity sentIdentity) {
      return this.identity.matches(sentIdentity);
   }

   public boolean matches(int metadata) {
      return this.identity.matches(metadata);
   }

   public boolean matches(int metadata, @Nullable NBTTagCompound compound) {
      return this.identity.matches(metadata, compound);
   }
}
