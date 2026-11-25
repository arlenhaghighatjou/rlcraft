package com.charles445.aireducer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public abstract class WrappedProximityTask extends WrappedTask {
   public double proximitySq = this.getProximity() * this.getProximity();

   public WrappedProximityTask(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public abstract boolean canModify();

   public abstract double getProximity();

   public boolean shouldExecute() {
      return this.canModify() && !this.isPlayerClose(this.entity) ? false : this.task.shouldExecute();
   }

   private boolean isPlayerClose(EntityLiving entity) {
      double eX = entity.posX;
      double eZ = entity.posZ;

      for(EntityPlayer player : entity.world.playerEntities) {
         if (!player.isSpectator() && this.getDistanceSqXZ(eX, eZ, player.posX, player.posZ) <= this.proximitySq) {
            return true;
         }
      }

      return false;
   }

   private double getDistanceSqXZ(double ax, double az, double bx, double bz) {
      double xx = ax - bx;
      double zz = az - bz;
      return xx * xx + zz * zz;
   }
}
