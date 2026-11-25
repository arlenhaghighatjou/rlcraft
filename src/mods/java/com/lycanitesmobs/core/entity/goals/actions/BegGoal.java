package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BegGoal extends EntityAIBase {
   private TameableCreatureEntity host;
   private EntityPlayer player;
   private float range = 64.0F;
   private int begTime;

   public BegGoal(TameableCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(3);
   }

   public BegGoal setRange(float setRange) {
      this.range = setRange * setRange;
      return this;
   }

   public boolean shouldExecute() {
      this.player = this.host.getEntityWorld().getClosestPlayerToEntity(this.host, (double)this.range);
      return this.player != null && this.gotBegItem(this.player);
   }

   public boolean shouldContinueExecuting() {
      return this.player.isEntityAlive() && (double)this.host.getDistance(this.player) <= (double)(this.range * this.range) && this.begTime > 0 && this.gotBegItem(this.player);
   }

   public void startExecuting() {
      this.host.setSitting(true);
      this.begTime = 40 + this.host.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.host.setSitting(false);
      this.player = null;
   }

   public void updateTask() {
      this.host.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double)this.player.getEyeHeight(), this.player.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
      --this.begTime;
   }

   private boolean gotBegItem(EntityPlayer player) {
      ItemStack itemstack = player.inventory.getCurrentItem();
      if (itemstack.isEmpty()) {
         return false;
      } else {
         return !this.host.isTamed() ? this.host.isTamingItem(itemstack) : this.host.isBreedingItem(itemstack);
      }
   }
}
