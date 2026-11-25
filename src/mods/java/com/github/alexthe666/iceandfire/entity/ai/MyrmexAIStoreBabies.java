package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIStoreBabies extends EntityAIBase {
   private final EntityMyrmexWorker myrmex;
   private final double movementSpeed;
   private Path path;
   private BlockPos nextRoom = BlockPos.ORIGIN;

   public MyrmexAIStoreBabies(EntityMyrmexWorker entityIn, double movementSpeedIn) {
      this.myrmex = entityIn;
      this.movementSpeed = movementSpeedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex.canMove() && this.myrmex.holdingBaby() && (this.myrmex.shouldEnterHive() || this.myrmex.getNavigator().noPath()) && !this.myrmex.canSeeSky()) {
         MyrmexHive village = this.myrmex.getHive();
         if (village == null) {
            return false;
         } else {
            this.nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.myrmex.getRNG(), this.myrmex.getPosition())).up();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.myrmex.holdingBaby() && !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(this.nextRoom) > 3.0D && this.myrmex.shouldEnterHive();
   }

   public void startExecuting() {
   }

   public void updateTask() {
      this.myrmex.getNavigator().tryMoveToXYZ((double)this.nextRoom.getX(), (double)this.nextRoom.getY(), (double)this.nextRoom.getZ(), this.movementSpeed);
      if (this.nextRoom != null && this.myrmex.getDistanceSq(this.nextRoom) < 4.0D && this.myrmex.holdingBaby() && !this.myrmex.getPassengers().isEmpty()) {
         for(Entity entity : this.myrmex.getPassengers()) {
            entity.dismountRidingEntity();
            this.resetTask();
            entity.copyLocationAndAnglesFrom(this.myrmex);
         }
      }

   }

   public void resetTask() {
      this.nextRoom = BlockPos.ORIGIN;
      this.myrmex.getNavigator().setPath((Path)null, this.movementSpeed);
   }
}
