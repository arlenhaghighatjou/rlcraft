package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionParasites extends PotionBase {
   private final ResourceLocation texture;
   private int savedDuration = 0;

   public PotionParasites() {
      super(false, 16769463);
      this.xOffset = 0;
      this.yOffset = 0;
      this.texture = this.formatTexture("parasites");
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityPlayer) {
         World world = entity.getEntityWorld();
         EntityPlayer player = (EntityPlayer)entity;
         double hunger = ModConfig.server.thirst.thirstParasitesHunger;
         if (hunger > 0.0D) {
            player.addExhaustion((float)hunger * (float)(amplifier + 1));
         }

         if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
            double poison = ModConfig.server.thirst.thirstParasitesDamage;
            if (poison > 0.0D && this.isReadyVar(this.savedDuration, amplifier, 25) && player.getRNG().nextDouble() < poison) {
               player.attackEntityFrom(SDDamageSources.PARASITES, 1.0F);
            }
         }
      }

   }

   public boolean isReady(int duration, int amplifier) {
      this.savedDuration = duration;
      return true;
   }
}
