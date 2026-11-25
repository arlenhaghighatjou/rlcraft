package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTremor extends TameableCreatureEntity implements IMob {
   public EntityTremor(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.isRareVariant() && !this.isPetType("familiar") && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1.0F && this.getRNG().nextInt(20) == 0) {
         if (this.posY - 1.0D > this.getAttackTarget().posY) {
            this.leap(6.0F, -1.0D, this.getAttackTarget());
         } else if (this.posY + 1.0D < this.getAttackTarget().posY) {
            this.leap(6.0F, 1.0D, this.getAttackTarget());
         } else {
            this.leap(6.0F, 0.0D, this.getAttackTarget());
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         int explosionStrength = Math.max(0, this.creatureInfo.getFlag("explosionStrength", 1));
         boolean damageTerrain = explosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing");
         if (this.isPetType("familiar")) {
            explosionStrength = 1;
            damageTerrain = false;
         }

         this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, (float)explosionStrength, damageTerrain);
         return true;
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      if (source.isExplosion()) {
         this.heal(damage);
         return false;
      } else {
         return !type.equals("cactus") && !type.equals("inWall") ? super.isDamageTypeApplicable(type, source, damage) : false;
      }
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      return entity instanceof EntityWither ? false : super.isDamageEntityApplicable(entity);
   }

   public boolean isPotionApplicable(PotionEffect effectInstance) {
      return effectInstance.getPotion() == MobEffects.WITHER ? false : super.isPotionApplicable(effectInstance);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean canBeTargetedBy(EntityLivingBase entity) {
      return entity instanceof EntityWither ? false : super.canBeTargetedBy(entity);
   }
}
