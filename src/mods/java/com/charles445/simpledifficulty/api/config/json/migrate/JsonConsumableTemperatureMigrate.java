package com.charles445.simpledifficulty.api.config.json.migrate;

import java.util.Locale;
import net.minecraft.item.ItemStack;

public class JsonConsumableTemperatureMigrate {
   public int metadata;
   public String group;
   public float temperature;
   public int duration;

   public JsonConsumableTemperatureMigrate(String group, float temperature, int metadata, int duration) {
      this.temperature = temperature;
      this.metadata = metadata;
      this.duration = duration;
      this.group = group.toLowerCase(Locale.ENGLISH);
   }

   public boolean matches(ItemStack stack) {
      return this.metadata == -1 || this.metadata == 32767 || this.metadata == stack.getMetadata();
   }

   public boolean matches(int meta) {
      return this.metadata == -1 || this.metadata == 32767 || this.metadata == meta;
   }
}
