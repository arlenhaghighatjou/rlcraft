package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIMoveThroughHive extends EntityAIBase {
   private final EntityMyrmexBase myrmex;
   private final double movementSpeed;
   private Path path;
   private BlockPos nextRoom = BlockPos.ORIGIN;

   public MyrmexAIMoveThroughHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
      this.myrmex = entityIn;
      this.movementSpeed = movementSpeedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.myrmex.canMove() && (!(this.myrmex instanceof EntityMyrmexWorker) || !((EntityMyrmexWorker)this.myrmex).holdingBaby()) && this.myrmex.shouldMoveThroughHive() && (this.myrmex.shouldEnterHive() || this.myrmex.getNavigator().noPath()) && !this.myrmex.canSeeSky() && (this.myrmex.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || !(this.myrmex instanceof EntityMyrmexWorker))) {
         MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(new BlockPos(this.myrmex), 300);
         if (village == null) {
            village = this.myrmex.getHive();
         }

         if (village == null) {
            return false;
         } else {
            this.nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(this.myrmex.getRNG(), this.myrmex.getPosition()));
            this.path = this.myrmex.getNavigator().getPathToPos(this.nextRoom);
            return this.path != null;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(this.nextRoom) > 3.0D && this.myrmex.shouldEnterHive() && (!(this.myrmex instanceof EntityMyrmexWorker) || !((EntityMyrmexWorker)this.myrmex).holdingBaby());
   }

   public void startExecuting() {
      this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
   }

   public void resetTask() {
      this.nextRoom = BlockPos.ORIGIN;
      this.myrmex.getNavigator().setPath((Path)null, this.movementSpeed);
   }
}
