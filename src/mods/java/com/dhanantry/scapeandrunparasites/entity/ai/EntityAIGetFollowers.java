package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIGetFollowers extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int version;
   private int searchRange;

   public EntityAIGetFollowers(EntityParasiteBase in, int mobVersion, int range) {
      this.parent = in;
      this.version = mobVersion;
      this.searchRange = range;
   }

   public boolean shouldExecute() {
      return this.parent.ticksExisted % 20 == 0 && this.parent.getParasiteFollowing() == null && this.parent.getAttackTarget() == null;
   }

   public void updateTask() {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow((double)this.searchRange, 2.0D, (double)this.searchRange);
      List<EntityParasiteBase> moblist = this.parent.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
      switch(this.version) {
      case 1:
         for(EntityParasiteBase mob : moblist) {
            if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive() && mob.getParasiteType() < 31) {
               if (mob.getParasiteFollowing() == null) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }

               if (mob.getParasiteFollowing().getParasiteType() <= 10) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }
            }
         }

         return;
      case 2:
         for(EntityParasiteBase mob : moblist) {
            if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive() && mob.getParasiteType() < 41) {
               if (mob.getParasiteFollowing() == null) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }

               if (mob.getParasiteFollowing().getParasiteType() <= 30) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }
            }
         }

         return;
      case 3:
         for(EntityParasiteBase mob : moblist) {
            if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive() && mob.getParasiteType() < 41) {
               if (mob.getParasiteFollowing() == null) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }

               if (mob.getParasiteFollowing().getParasiteType() <= 40) {
                  mob.setParasiteToFollow(this.parent);
                  return;
               }
            }
         }

         return;
      case 4:
         for(EntityParasiteBase mob : moblist) {
            if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive() && mob.getParasiteType() < 61) {
               if (mob.getParasiteFollowing() == null) {
                  mob.setParasiteToFollow(this.parent);
                  break;
               }

               if (mob.getParasiteFollowing().getParasiteType() <= 60) {
                  mob.setParasiteToFollow(this.parent);
                  break;
               }
            }
         }
      }

   }
}
