package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.MathHelper;

public class ForceGoal extends EntityAIBase {
   BaseCreatureEntity host;
   protected int duration = 200;
   protected int cooldownDuration = 300;
   protected int windUp = 60;
   protected float range = 300.0F;
   protected float force = 1.0F;
   protected int phase = -1;
   public int abilityTime = 0;
   public int cooldownTime;
   public boolean windUpForce;
   public boolean dismountTargets;

   public ForceGoal(BaseCreatureEntity setHost) {
      this.cooldownTime = this.cooldownDuration;
      this.windUpForce = false;
      this.dismountTargets = false;
      this.host = setHost;
   }

   public ForceGoal setPhase(int phase) {
      this.phase = phase;
      return this;
   }

   public ForceGoal setDuration(int duration) {
      this.duration = duration;
      return this;
   }

   public ForceGoal setCooldown(int cooldown) {
      this.cooldownDuration = cooldown;
      this.cooldownTime = cooldown;
      return this;
   }

   public ForceGoal setWindUp(int windUp) {
      this.windUp = windUp;
      return this;
   }

   public ForceGoal setRange(float range) {
      this.range = range;
      return this;
   }

   public ForceGoal setForce(float force) {
      this.force = force;
      return this;
   }

   public ForceGoal setWindUpForce(boolean windUpForce) {
      this.windUpForce = windUpForce;
      return this;
   }

   public ForceGoal setDismount(boolean dismountTargets) {
      this.dismountTargets = dismountTargets;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.isEntityAlive()) {
         return false;
      } else {
         return this.phase < 0 || this.phase == this.host.getBattlePhase();
      }
   }

   public void startExecuting() {
      this.cooldownTime = this.cooldownDuration;
   }

   public void updateTask() {
      if (this.cooldownTime-- > 0) {
         this.abilityTime = 0;
      } else {
         if (this.abilityTime == this.windUp) {
            this.host.playAttackSound();
         }

         if (this.abilityTime++ >= this.duration && this.cooldownDuration > 0) {
            this.cooldownTime = this.cooldownDuration;
         } else {
            double motionCap = (double)(-this.force);
            double factor = (double)(-this.force) * 0.1D;
            if (this.abilityTime < this.windUp) {
               if (!this.windUpForce) {
                  return;
               }

               factor *= (double)this.abilityTime / (double)this.windUp;
            }

            for(Entity entity : this.host.getNearbyEntities(Entity.class, this::isValidTarget, (double)this.range)) {
               if (entity instanceof EntityLivingBase) {
                  double xDist = this.host.posX - entity.posX;
                  double yDist = this.host.posY - entity.posY;
                  double zDist = this.host.posZ - entity.posZ;
                  double xzDist = Math.max((double)MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
                  EntityPlayerMP player = null;
                  if (entity instanceof EntityPlayerMP) {
                     player = (EntityPlayerMP)entity;
                  }

                  if (entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
                     entity.addVelocity(xDist / xzDist * factor + entity.motionX * factor, yDist * factor * 0.25D + entity.motionY * factor * 0.25D, zDist / xzDist * factor + entity.motionZ * factor);
                  }

                  if (this.dismountTargets && entity.getRidingEntity() != null) {
                     entity.dismountRidingEntity();
                  }

                  if (player != null) {
                     player.connection.sendPacket(new SPacketEntityVelocity(entity));
                  }
               }
            }

         }
      }
   }

   public boolean isValidTarget(Entity entity) {
      if (entity == this.host) {
         return false;
      } else {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.capabilities.isCreativeMode || player.isSpectator()) {
               return false;
            }
         }

         return true;
      }
   }
}
