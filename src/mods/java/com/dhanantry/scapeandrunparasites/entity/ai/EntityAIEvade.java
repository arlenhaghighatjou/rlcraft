package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;

public class EntityAIEvade extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int eCooldown;
   private int cooldown;
   private int eDuration;
   private int tracker;
   private boolean inC;
   private int blockDistance;

   public EntityAIEvade(EntityParasiteBase in, int cooldown, int duration, int distance) {
      this.parent = in;
      this.tracker = 0;
      this.eCooldown = cooldown;
      this.cooldown = cooldown + 1;
      this.eDuration = duration;
      this.blockDistance = distance * distance;
      this.inC = false;
   }

   public boolean shouldExecute() {
      return (this.parent.getAttackTarget() != null || this.inC) && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround;
   }

   public void resetTask() {
      this.cooldown = 0;
      this.tracker = 0;
      this.inC = false;
      this.parent.moveStrafing = 0.0F;
   }

   public void updateTask() {
      EntityLivingBase target = this.parent.getAttackTarget();
      if ((target != null || this.inC) && !this.parent.isPotionActive(MobEffects.SLOWNESS)) {
         if (this.inC) {
            ++this.tracker;
            if (this.tracker >= this.eDuration) {
               this.parent.moveStrafing = 0.0F;
               this.tracker = 0;
               this.cooldown = 0;
               this.inC = false;
            }
         } else {
            if (this.parent.getDistanceSq(target) > (double)this.blockDistance && this.parent.canEntityBeSeen(target) && this.parent.getDistanceSq(target) < 225.0D) {
               ++this.cooldown;
            }

            if (this.cooldown >= this.eCooldown && this.parent.moveStrafing == 0.0F) {
               int i = this.parent.getRNG().nextInt(2) == 0 ? 1 : -1;
               this.parent.moveStrafing = (float)i;
               this.inC = true;
               double dd0 = target.posX - this.parent.posX;
               double dd1 = target.posZ - this.parent.posZ;
               float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
               this.parent.motionX += dd0 / (double)f * 1.77D * 0.800000011920929D + this.parent.motionX * 0.20000000298023224D;
               this.parent.motionZ += dd1 / (double)f * 1.77D * 0.800000011920929D + this.parent.motionZ * 0.20000000298023224D;
               this.parent.motionY = 0.2D + (double)this.parent.height * 0.1D;
               this.parent.getNavigator().clearPath();
            }
         }
      }

   }
}
