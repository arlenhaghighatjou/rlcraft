package dev.satyrn.wolfarmor.entity.ai;

import dev.satyrn.wolfarmor.WolfArmorMod;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class EntityAIWolfHowl extends EntityAIBase {
   private final EntityWolf entity;
   private final EaglercraftRandom random;
   private int soundTimer;

   public EntityAIWolfHowl(EntityWolf entity) {
      this.entity = entity;
      this.random = entity.getRNG();
   }

   public boolean shouldExecute() {
      if (!WolfArmorMod.getConfig().getHowlAtMoonEnabled()) {
         return false;
      } else {
         World entityWorld = this.entity.getEntityWorld();
         int phase = entityWorld.provider.getMoonPhase(entityWorld.getWorldTime());
         return !this.entity.isTamed() && phase == 0 && !entityWorld.isDaytime();
      }
   }

   public void startExecuting() {
      this.soundTimer = 300 + this.random.nextInt(600) + this.random.nextInt(600) + this.random.nextInt(600) + this.random.nextInt(600);
   }

   public boolean shouldContinueExecuting() {
      World entityWorld = this.entity.getEntityWorld();
      int phase = entityWorld.provider.getMoonPhase(entityWorld.getWorldTime());
      if (!this.entity.isTamed() && phase == 0 && !entityWorld.isDaytime()) {
         return this.soundTimer > 0;
      } else {
         return false;
      }
   }

   public void updateTask() {
      if (this.soundTimer > 0) {
         --this.soundTimer;
      }

   }

   public void resetTask() {
      World entityWorld = this.entity.getEntityWorld();
      int phase = entityWorld.provider.getMoonPhase(entityWorld.getWorldTime());
      if (!this.entity.isTamed() && phase == 0 && !entityWorld.isDaytime()) {
         this.entity.playSound(SoundEvents.ENTITY_WOLF_HOWL, 5.0F, 0.75F + this.random.nextFloat() * (this.entity.isChild() ? 1.25F : 0.25F));
      }

   }
}
