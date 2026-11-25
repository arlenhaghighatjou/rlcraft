package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityNymph extends TameableCreatureEntity {
   public int healingRate = 20;
   private int farmingTick = 0;

   public EntityNymph(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
   }

   public void loadCreatureFlags() {
      this.healingRate = this.creatureInfo.getFlag("healingRate", this.healingRate);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.healingRate > 0 && !this.isPetType("familiar") && this.updateTick % (long)this.healingRate == 0L) {
            for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
               EntityLivingBase target = (EntityLivingBase)entityObj;
               if (target != this && !(target instanceof EntityNymph) && target != this.getAttackTarget() && target != this.getAvoidTarget()) {
                  target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 0));
               }
            }
         }

         if (this.hasAvoidTarget()) {
            if (this.updateTick % (long)this.getRangedCooldown() == 0L) {
               this.attackRanged(this.getAvoidTarget(), this.getDistance(this.getAvoidTarget()));
            }
         } else if (this.hasAttackTarget() && this.updateTick % (long)this.getRangedCooldown() == 0L) {
            this.attackRanged(this.getAttackTarget(), this.getDistance(this.getAttackTarget()));
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("faebolt", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.75F, 1.0F, 1.0F);
      super.attackRanged(target, range);
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
}
