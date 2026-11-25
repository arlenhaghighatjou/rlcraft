package com.charles445.simpledifficulty.api.config.json;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsonConsumableTemperature {
   public JsonItemIdentity identity;
   public String group;
   public float temperature;
   public int duration;

   public JsonConsumableTemperature(String group, float temperature, int duration, int metadata) {
      this(group, temperature, duration, new JsonItemIdentity(metadata));
   }

   public JsonConsumableTemperature(String group, float temperature, int duration, int metadata, String nbt) {
      this(group, temperature, duration, new JsonItemIdentity(metadata, nbt));
   }

   public JsonConsumableTemperature(String group, float temperature, int duration, JsonItemIdentity identity) {
      this.temperature = temperature;
      this.duration = duration;
      this.group = group.toLowerCase(Locale.ENGLISH);
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
