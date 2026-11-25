package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionResistCold extends PotionBase {
   private final ResourceLocation texture;

   public PotionResistCold() {
      super(false, 9368063);
      this.xOffset = 0;
      this.yOffset = 0;
      this.texture = this.formatTexture("resist_cold");
      this.setBeneficial();
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      this.removePotionCoreEffect(entity, SDPotions.hypothermia);
   }
}
