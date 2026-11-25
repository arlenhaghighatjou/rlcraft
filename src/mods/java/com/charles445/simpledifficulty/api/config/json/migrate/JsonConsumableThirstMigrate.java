package com.charles445.simpledifficulty.api.config.json.migrate;

import net.minecraft.item.ItemStack;

public class JsonConsumableThirstMigrate {
   public int metadata;
   public int amount;
   public float saturation;
   public float thirstyChance;

   public JsonConsumableThirstMigrate(int metadata, int amount, float saturation, float thirstyChance) {
      this.metadata = metadata;
      this.amount = amount;
      this.saturation = saturation;
      this.thirstyChance = thirstyChance;
   }

   public boolean matches(ItemStack stack) {
      return this.metadata == -1 || this.metadata == 32767 || this.metadata == stack.getMetadata();
   }

   public boolean matches(int meta) {
      return this.metadata == -1 || this.metadata == 32767 || this.metadata == meta;
   }
}
