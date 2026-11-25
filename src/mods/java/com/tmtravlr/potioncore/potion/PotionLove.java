package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class PotionLove extends PotionCorePotion {
   public static final String NAME = "love";
   public static final PotionLove INSTANCE = new PotionLove();

   public PotionLove() {
      super("love", false, 16724787);
   }

   public boolean isInstant() {
      return true;
   }

   public void affectEntity(Entity thrownPotion, Entity thrower, EntityLivingBase entity, int amplifier, double potency) {
      if (entity instanceof EntityAnimal) {
         EntityPlayer player = null;
         if (thrower instanceof EntityPlayer) {
            player = (EntityPlayer)thrower;
         }

         ((EntityAnimal)entity).setInLove(player);
      } else {
         this.performEffect(entity, amplifier);
      }

   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityAnimal) {
         ((EntityAnimal)entity).setInLove((EntityPlayer)null);
      } else if (entity.world instanceof WorldServer) {
         ((WorldServer)entity.world).spawnParticle(EnumParticleTypes.HEART, true, entity.posX, entity.posY, entity.posZ, 20, 0.5D, 2.0D, 0.5D, 0.0D, new int[0]);
      }

   }
}
