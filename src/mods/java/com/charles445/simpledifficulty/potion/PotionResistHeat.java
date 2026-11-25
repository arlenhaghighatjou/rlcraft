package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionResistHeat extends PotionBase {
   private final ResourceLocation texture;

   public PotionResistHeat() {
      super(false, 16764274);
      this.xOffset = 0;
      this.yOffset = 0;
      this.texture = this.formatTexture("resist_heat");
      this.setBeneficial();
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      this.removePotionCoreEffect(entity, SDPotions.hyperthermia);
   }
}
