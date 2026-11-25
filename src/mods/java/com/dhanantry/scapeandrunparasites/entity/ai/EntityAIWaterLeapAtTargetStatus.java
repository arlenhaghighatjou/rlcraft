package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class EntityAIWaterLeapAtTargetStatus extends EntityAIBase {
   private EntityParasiteBase leaper;
   private EntityLivingBase leapTarget;
   private float leapMotionY;
   private float targetY;
   private double jumpSpeed;
   private int jCooldown;
   private int jumpR;
   private int attackTimer = 0;
   private int attacking = 0;
   private double targetX;
   private double targetZ;

   public EntityAIWaterLeapAtTargetStatus(EntityParasiteBase leapingEntity, float leapMotionYIn, double speed, int distance, int cooldown, int jumpDamageRange) {
      this.leaper = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.jumpSpeed = speed;
      this.jCooldown = cooldown;
      this.jumpR = jumpDamageRange;
   }

   public boolean shouldExecute() {
      return this.leaper.isInWater() || this.leaper.isInLava() || this.attacking >= 1;
   }

   public void updateTask() {
      if (this.leaper.getAttackTarget() != null && this.leaper.shouldWorkTask()) {
         if (!this.leaper.getAttackTarget().isDead && this.leaper.getParasiteStatus() <= 2) {
            EntityLivingBase entitylivingbase = this.leaper.getAttackTarget();
            ++this.attackTimer;
            if (this.attackTimer >= this.jCooldown && this.attacking == 0) {
               ++this.attacking;
               this.targetX = entitylivingbase.posX;
               this.targetZ = entitylivingbase.posZ;
               this.targetY = (float)(entitylivingbase.posY - this.leaper.posY) * 0.07F;
               if (this.targetY <= 0.0F) {
                  this.targetY = 0.0F;
               }
            }
         } else if (this.attackTimer > 0) {
            --this.attackTimer;
         }
      } else if (this.attackTimer > 0) {
         --this.attackTimer;
      }

      if (this.attacking >= 1) {
         ++this.attacking;
         if (this.attacking == 2 && this.leaper.onGround) {
            this.leaper.setParasiteStatus(10);
            if (this.leaper.getNavigator().getPath() != null) {
               this.leaper.getNavigator().clearPath();
            }

            double d0 = this.targetX - this.leaper.posX;
            double d1 = this.targetZ - this.leaper.posZ;
            double f = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
            this.leaper.motionY = (double)this.leapMotionY + (double)this.targetY;
            this.leaper.motionX += d0 / f * this.jumpSpeed * 0.9D + this.leaper.motionX * 0.3D;
            this.leaper.motionZ += d1 / f * this.jumpSpeed * 0.9D + this.leaper.motionZ * 0.3D;
         }

         if (this.attacking >= 3 && this.leaper.onGround) {
            if (this.jumpR != 0) {
               float damage = (float)this.leaper.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.leaper.posX, this.leaper.posY, this.leaper.posZ, this.leaper.posX + 1.0D, this.leaper.posY + 1.0D, this.leaper.posZ + 1.0D)).grow((double)this.jumpR, 2.0D, (double)this.jumpR);

               for(EntityLivingBase mob : this.leaper.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                  if (mob != this.leaper && !(mob instanceof EntityParasiteBase)) {
                     mob.knockBack(mob, 2.5F, this.leaper.posX - mob.posX, this.leaper.posZ - mob.posZ);
                     this.leaper.attackEntityAsMob(mob);
                  }
               }
            }

            this.attacking = 0;
            this.attackTimer = 0;
            this.leaper.setParasiteStatus(2);
         }
      }

   }
}
