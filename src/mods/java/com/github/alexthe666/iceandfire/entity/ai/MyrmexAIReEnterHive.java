package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIReEnterHive extends EntityAIBase {
   private final EntityMyrmexBase myrmex;
   private final double movementSpeed;
   private Path path;
   private BlockPos nextEntrance = BlockPos.ORIGIN;
   private boolean first = true;
   private MyrmexHive hive;

   public MyrmexAIReEnterHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
      this.myrmex = entityIn;
      this.movementSpeed = movementSpeedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex.canMove() && !this.myrmex.shouldLeaveHive() && this.myrmex.canSeeSky() && this.first) {
         MyrmexHive village = this.myrmex.getHive();
         if (village == null) {
            village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(new BlockPos(this.myrmex), 500);
         }

         if (village == null) {
            return false;
         } else {
            this.hive = village;
            this.nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, this.hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), false));
            this.path = this.myrmex.getNavigator().getPathToPos(this.nextEntrance);
            this.first = true;
            return this.path != null;
         }
      } else {
         return false;
      }
   }

   public void updateTask() {
      if (this.first) {
         this.hive.setWorld(this.myrmex.world);
         this.nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, this.hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), false));
      }

      this.path = this.myrmex.getNavigator().getPathToPos(this.nextEntrance);
      this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
      if (this.myrmex.getDistanceSq(this.nextEntrance) < 9.0D && this.first && this.hive != null) {
         this.nextEntrance = this.hive.getClosestEntranceBottomToEntity(this.myrmex, this.myrmex.getRNG());
         this.first = false;
         this.myrmex.getNavigator().clearPath();
         this.path = this.myrmex.getNavigator().getPathToPos(this.nextEntrance);
         this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
      }

      if (this.myrmex.getDistanceSq(this.nextEntrance) < 15.0D && !this.first) {
         this.myrmex.isEnteringHive = false;
      } else {
         this.myrmex.isEnteringHive = true;
      }

   }

   public boolean shouldContinueExecuting() {
      return this.myrmex.getDistanceSq(this.nextEntrance) < 15.0D && !this.first ? false : this.myrmex.shouldEnterHive();
   }

   public void resetTask() {
      this.nextEntrance = BlockPos.ORIGIN;
      this.myrmex.getNavigator().setPath((Path)null, this.movementSpeed);
      this.first = true;
   }
}
