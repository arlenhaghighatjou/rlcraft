package com.creativemd.creativecore.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySit extends Entity {
   public EntitySit(World world, double x, double y, double z) {
      super(world);
      this.noClip = true;
      this.preventEntitySpawning = true;
      this.width = 0.0F;
      this.height = 0.0F;
      this.setPosition(x, y, z);
   }

   public EntitySit(World world) {
      super(world);
      this.noClip = true;
      this.preventEntitySpawning = true;
      this.width = 0.0F;
      this.height = 0.0F;
   }

   public boolean canBePushed() {
      return false;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && !this.isBeingRidden()) {
         this.setDead();
      }

   }

   public double getMountedYOffset() {
      return 0.0D;
   }

   protected boolean isAIEnabled() {
      return false;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound nbt) {
   }

   protected void writeEntityToNBT(NBTTagCompound nbt) {
   }
}
