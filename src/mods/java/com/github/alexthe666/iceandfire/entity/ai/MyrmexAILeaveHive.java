package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class MyrmexAILeaveHive extends EntityAIBase {
   private final EntityMyrmexBase myrmex;
   private final double movementSpeed;
   private Path path;
   private BlockPos nextEntrance = BlockPos.ORIGIN;

   public MyrmexAILeaveHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
      this.myrmex = entityIn;
      this.movementSpeed = movementSpeedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex instanceof EntityMyrmexQueen) {
         return false;
      } else if (this.myrmex.isChild()) {
         return false;
      } else {
         if (this.myrmex instanceof EntityMyrmexSentinel) {
            ;
         }

         if (this.myrmex.canMove() && this.myrmex.shouldLeaveHive() && !this.myrmex.canSeeSky() && (!(this.myrmex instanceof EntityMyrmexWorker) || !((EntityMyrmexWorker)this.myrmex).holdingBaby() && this.myrmex.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) && !this.myrmex.isEnteringHive) {
            MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(new BlockPos(this.myrmex), 1000);
            if (village == null) {
               return false;
            } else {
               this.nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, village.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), true));
               this.path = this.myrmex.getNavigator().getPathToPos(this.nextEntrance);
               return this.path != null;
            }
         } else {
            return false;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.myrmex.getDistanceSq(this.nextEntrance) > 3.0D && !this.myrmex.shouldEnterHive()) {
         return !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(this.nextEntrance) > 3.0D && this.myrmex.shouldLeaveHive();
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
   }

   public void resetTask() {
      this.nextEntrance = BlockPos.ORIGIN;
      this.myrmex.getNavigator().setPath((Path)null, this.movementSpeed);
   }
}
