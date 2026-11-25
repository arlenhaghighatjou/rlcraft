package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class PotionThirsty extends PotionBase {
   private final ResourceLocation texture;

   public PotionThirsty() {
      super(true, 2856192);
      this.xOffset = 0;
      this.yOffset = 0;
      this.texture = this.formatTexture("thirsty");
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (entityLivingBaseIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
         IThirstCapability capability = SDCapabilities.getThirstData(player);
         capability.addThirstExhaustion((float)ModConfig.server.thirst.thirstyStrength * (float)(1 + amplifier));
      }

   }
}
