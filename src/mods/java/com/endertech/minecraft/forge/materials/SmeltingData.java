package com.endertech.minecraft.forge.materials;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;

public class SmeltingData implements IHaveConfig {
   private final ForgeConfig config;
   private final int amount;
   private final float xp;

   public SmeltingData() {
      this((ForgeConfig)null, (String)null, 1, 0.0F);
   }

   public SmeltingData(ForgeConfig config, String category, ForgeMaterial material) {
      this(config, category, 1, ForgeBounds.SMELTING_XP.getFloatBounds().approxUp(material.getStrength()).floatValue());
   }

   public SmeltingData(ForgeConfig config, String category, int amount, float xp) {
      category = this.expandClassCategory(category);
      this.config = config;
      this.amount = ForgeConfig.getInt(config, category, "amount", amount, ForgeBounds.STACK_SIZE.getIntBounds(), "How many items will be smelted from this ore");
      this.xp = ForgeConfig.getFloat(config, category, "xp", xp, ForgeBounds.EXPERIENCE.getFloatBounds(), "How many experience will be gained from smelting");
   }

   public String toString() {
      return SmeltingData.class.getSimpleName() + Args.group(Args.get("amount", Integer.valueOf(this.getAmount())), Args.get("xp", Float.valueOf(this.getXp())));
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public int getAmount() {
      return this.amount;
   }

   public float getXp() {
      return this.xp;
   }
}
