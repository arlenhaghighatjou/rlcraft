package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;

public class BreakDoorGoal extends DoorInteractGoal {
   private int breakingTime;
   private int lastBreakTime = -1;

   public BreakDoorGoal(EntityLiving setHost) {
      super(setHost);
   }

   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else if (!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else {
         BlockDoor var10000 = this.targetDoor;
         return !BlockDoor.isOpen(this.host.getEntityWorld(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));
      }
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public boolean shouldContinueExecuting() {
      double distance = this.host.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
      boolean var3;
      if (this.breakingTime <= 240) {
         BlockDoor var10000 = this.targetDoor;
         if (!BlockDoor.isOpen(this.host.getEntityWorld(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ)) && distance < 4.0D) {
            var3 = true;
            return var3;
         }
      }

      var3 = false;
      return var3;
   }

   public void resetTask() {
      super.resetTask();
      this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), -1);
   }

   public void updateTask() {
      super.updateTask();
      if (this.host.getRNG().nextInt(20) == 0) {
         this.host.getEntityWorld().playEvent(1010, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
      }

      ++this.breakingTime;
      int breaking = (int)((float)this.breakingTime / 240.0F * 10.0F);
      if (breaking != this.lastBreakTime) {
         this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), breaking);
         this.lastBreakTime = breaking;
      }

      if (this.breakingTime == 240 && this.host.getEntityWorld().getDifficulty() == EnumDifficulty.HARD) {
         this.host.getEntityWorld().setBlockToAir(new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));
         this.host.getEntityWorld().playEvent(1012, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
         this.host.getEntityWorld().playEvent(2001, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
      }

   }
}
