package com.oblivioussp.spartanweaponry.mixin;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Entity.class})
public class EntityMixin {
   @Shadow
   public World world;
   @Shadow
   public EaglercraftRandom rand;
   @Shadow
   public double posX;
   @Shadow
   public double posY;
   @Shadow
   public double posZ;

   public EntityMixin() {
   }

   @Shadow
   public int getEntityId() {
      throw new IllegalStateException("Mixin failed to shadow the \"Entity.getEntityId()\" method!");
   }

   @Shadow
   public void setDead() {
      throw new IllegalStateException("Mixin failed to shadow the \"Entity.setDead()\" method!");
   }
}
