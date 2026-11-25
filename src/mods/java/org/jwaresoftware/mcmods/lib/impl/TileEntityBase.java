package org.jwaresoftware.mcmods.lib.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBase extends TileEntity {
   protected TileEntityBase() {
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new SPacketUpdateTileEntity(this.pos, 0, nbtTagCompound);
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }
}
