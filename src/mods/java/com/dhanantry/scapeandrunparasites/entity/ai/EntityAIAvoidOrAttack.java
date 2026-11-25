package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIAvoidOrAttack extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int tickss;
   private float light;

   public EntityAIAvoidOrAttack(EntityParasiteBase in, float lightR) {
      this.parent = in;
      this.tickss = 20;
      this.light = lightR;
      this.parent.setWorkTask(false);
   }

   public boolean shouldExecute() {
      --this.tickss;
      if (this.tickss < 0) {
         this.tickss = 20;
         return this.parent.getParasiteFollowing() == null;
      } else {
         return false;
      }
   }

   public void updateTask() {
      float f = this.parent.getBrightness();
      if (this.parent.isPotionActive(MobEffects.NIGHT_VISION) && this.parent.getParasiteType() < 16) {
         this.parent.setWorkTask(true);
      } else {
         if (f >= this.light) {
            boolean flag = true;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(10.0D, 2.0D, 10.0D);

            for(EntityParasiteBase mob : this.parent.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
               if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive()) {
                  if (flag) {
                     flag = false;
                     this.parent.setWorkTask(true);
                  }

                  if (this.parent.getParasiteFollowing() == null) {
                     if (mob.getParasiteFollowing() == null) {
                        this.parent.setParasiteToFollow(mob);
                     } else if (mob.getParasiteFollowing().getParasiteType() >= 11) {
                        this.parent.setParasiteToFollow(mob.getParasiteFollowing());
                     }
                  }

                  mob.setWorkTask(true);
               }
            }

            if (flag) {
               this.parent.setWorkTask(false);
            }
         } else {
            this.parent.setWorkTask(true);
         }

      }
   }
}
