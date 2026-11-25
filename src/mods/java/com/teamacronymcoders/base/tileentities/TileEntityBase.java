package com.teamacronymcoders.base.tileentities;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBase extends TileEntity {
   public TileEntityBase() {
   }

   public void readFromNBT(NBTTagCompound data) {
      this.readFromDisk(data);
      super.readFromNBT(data);
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound data) {
      data = this.writeToDisk(data);
      return super.writeToNBT(data);
   }

   protected abstract void readFromDisk(NBTTagCompound var1);

   protected abstract NBTTagCompound writeToDisk(NBTTagCompound var1);

   public void sendBlockUpdate() {
      if (!this.getWorld().isRemote) {
         this.getWorld().notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.pos), this.getWorld().getBlockState(this.pos), 3);
      }

   }
}
