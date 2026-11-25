package com.charles445.simpledifficulty.api.config.json;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class JsonConsumableThirst {
   public JsonItemIdentity identity;
   public int amount;
   public float saturation;
   public float thirstyChance;

   public JsonConsumableThirst(int amount, float saturation, float thirstyChance, int metadata) {
      this(amount, saturation, thirstyChance, new JsonItemIdentity(metadata));
   }

   public JsonConsumableThirst(int amount, float saturation, float thirstyChance, int metadata, String nbt) {
      this(amount, saturation, thirstyChance, new JsonItemIdentity(metadata, nbt));
   }

   public JsonConsumableThirst(int amount, float saturation, float thirstyChance, JsonItemIdentity identity) {
      this.amount = amount;
      this.saturation = saturation;
      this.thirstyChance = thirstyChance;
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
