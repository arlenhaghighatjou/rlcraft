package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class PotionThermia extends PotionBase {
   public PotionThermia(boolean isBadEffect, int liquidColor) {
      super(isBadEffect, liquidColor);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityPlayer && !entity.isPotionActive(SDPotions.heat_resist)) {
         World world = entity.getEntityWorld();
         EntityPlayer player = (EntityPlayer)entity;
         if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
            ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
            float damage = 0.5F + 0.5F * (float)capability.getTemperatureDamageCounter() * (float)ModConfig.server.temperature.temperatureDamageScaling;
            this.attackPlayer(player, damage);
            capability.addTemperatureDamageCounter(1);
         }
      }

   }

   public abstract void attackPlayer(EntityPlayer var1, float var2);
}
