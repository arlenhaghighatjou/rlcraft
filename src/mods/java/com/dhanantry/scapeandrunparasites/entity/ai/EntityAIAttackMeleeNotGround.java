package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackMeleeNotGround extends EntityAIBase {
   protected EntityParasiteBase attacker;
   protected int attackTick;
   private double attack;
   private double track;
   private int cooldown;
   private int currentCD;
   private double speed;
   private boolean needAir;

   public EntityAIAttackMeleeNotGround(EntityParasiteBase creature, double attackD, double tracking, double spee, boolean air) {
      this.attacker = creature;
      this.attack = attackD * attackD;
      this.track = tracking * tracking;
      this.cooldown = 0;
      this.currentCD = 80;
      this.speed = spee;
      this.needAir = air;
   }

   public EntityAIAttackMeleeNotGround(EntityParasiteBase creature, double attackD, double tracking, double spee, boolean air, int cd) {
      this(creature, attackD, tracking, spee, air);
      this.currentCD = cd;
   }

   public boolean shouldExecute() {
      return this.attacker.getAttackTarget() != null;
   }

   public void updateTask() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (entitylivingbase != null) {
         if (!entitylivingbase.isEntityAlive()) {
            this.attacker.setAttackTarget((EntityLivingBase)null);
         } else {
            if (this.needAir) {
               if (this.attacker.isInWater()) {
                  return;
               }
            } else if (!this.attacker.isInWater()) {
               return;
            }

            ++this.cooldown;
            if (this.cooldown >= this.currentCD) {
               this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
               double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
               if (this.attacker.getDistanceSq(entitylivingbase) < this.track && this.attacker.canEntityBeSeen(entitylivingbase)) {
                  double dd0 = entitylivingbase.posX - this.attacker.posX;
                  double dd1 = entitylivingbase.posZ - this.attacker.posZ;
                  double yy = entitylivingbase.posY >= this.attacker.posY ? 1.0D : -1.0D;
                  float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
                  this.attacker.motionX += dd0 / (double)f * this.speed + this.attacker.motionX * this.speed;
                  this.attacker.motionZ += dd1 / (double)f * this.speed + this.attacker.motionZ * this.speed;
                  this.attacker.motionY = 0.2D * yy;
                  this.attackTick = Math.max(this.attackTick - 1, 0);
                  this.checkAndPerformAttack(entitylivingbase, d0);
                  if (this.cooldown > 140) {
                     this.cooldown = 0;
                  }
               }

            }
         }
      }
   }

   protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
      if (distance <= this.attack && this.attackTick <= 0) {
         this.attackTick = 20;
         EntityCutomAttack thisA = (EntityCutomAttack)this.attacker;
         thisA.attackEntityAsMobAOE(target);
      }

   }
}
