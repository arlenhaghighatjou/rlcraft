package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class MoveVillageGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private double speed;
   private boolean isNocturnal = true;
   private Path entityPathNavigate;
   private VillageDoorInfo doorInfo;
   private List doorList = new ArrayList();

   public MoveVillageGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public MoveVillageGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public MoveVillageGoal setNocturnal(boolean flag) {
      this.isNocturnal = flag;
      return this;
   }

   public boolean shouldExecute() {
      this.shuffleDoorList();
      if (this.isNocturnal && this.host.getEntityWorld().isDaytime()) {
         return false;
      } else {
         Village village = this.host.getEntityWorld().getVillageCollection().getNearestVillage(new BlockPos(MathHelper.floor(this.host.posX), MathHelper.floor(this.host.posY), MathHelper.floor(this.host.posZ)), 0);
         if (village == null) {
            return false;
         } else {
            this.doorInfo = this.getDoorInfo(village);
            if (this.doorInfo == null) {
               return false;
            } else if (!(this.host.getNavigator() instanceof CreaturePathNavigate)) {
               return false;
            } else {
               CreaturePathNavigate pathNavigate = (CreaturePathNavigate)this.host.getNavigator();
               boolean flag = pathNavigate.getEnterDoors();
               pathNavigate.setCanOpenDoors(false);
               this.entityPathNavigate = this.host.getNavigator().getPathToXYZ((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsideBlockPos().getY(), (double)this.doorInfo.getInsideOffsetZ());
               pathNavigate.setCanOpenDoors(flag);
               if (this.entityPathNavigate != null) {
                  return true;
               } else {
                  Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 10, 7, new Vec3d((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsideBlockPos().getY(), (double)this.doorInfo.getInsideOffsetZ()));
                  if (vec3 == null) {
                     return false;
                  } else {
                     pathNavigate.setCanOpenDoors(false);
                     this.entityPathNavigate = this.host.getNavigator().getPathToXYZ(vec3.x, vec3.y, vec3.z);
                     pathNavigate.setCanOpenDoors(flag);
                     return this.entityPathNavigate != null;
                  }
               }
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.host.getNavigator().noPath()) {
         return false;
      } else {
         float f = this.host.width + 4.0F;
         return this.host.getDistanceSq((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsideBlockPos().getY(), (double)this.doorInfo.getInsideOffsetZ()) > (double)(f * f);
      }
   }

   public void startExecuting() {
      this.host.getNavigator().setPath(this.entityPathNavigate, this.speed);
   }

   public void resetTask() {
      if (this.host.getNavigator().noPath() || this.host.getDistanceSq((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsideBlockPos().getY(), (double)this.doorInfo.getInsideOffsetZ()) < 16.0D) {
         this.doorList.add(this.doorInfo);
      }

   }

   private VillageDoorInfo getDoorInfo(Village par1Village) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : par1Village.getVillageDoorInfoList()) {
         int j = villagedoorinfo1.getDistanceSquared(MathHelper.floor(this.host.posX), MathHelper.floor(this.host.posY), MathHelper.floor(this.host.posZ));
         if (j < i && !this.doesDoorListContain(villagedoorinfo1)) {
            villagedoorinfo = villagedoorinfo1;
            i = j;
         }
      }

      return villagedoorinfo;
   }

   private boolean doesDoorListContain(VillageDoorInfo par1VillageDoorInfo) {
      for(VillageDoorInfo villagedoorinfo1 : this.doorList) {
         if (par1VillageDoorInfo.getInsideOffsetX() == villagedoorinfo1.getInsideOffsetX() && par1VillageDoorInfo.getInsideBlockPos().getY() == villagedoorinfo1.getInsideBlockPos().getY() && par1VillageDoorInfo.getInsideOffsetZ() == villagedoorinfo1.getInsideOffsetZ()) {
            return true;
         }
      }

      return false;
   }

   private void shuffleDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }

   }
}
