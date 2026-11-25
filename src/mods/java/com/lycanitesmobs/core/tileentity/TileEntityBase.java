package com.lycanitesmobs.core.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public abstract class TileEntityBase extends TileEntity implements ITickable, ISidedInventory {
   protected boolean destroyed = false;

   public TileEntityBase() {
   }

   public void onRemove() {
      this.destroyed = true;
   }

   public void update() {
   }

   public boolean receiveClientEvent(int eventID, int eventArg) {
      return false;
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
   }

   public void onGuiButton(byte buttonId) {
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      return super.writeToNBT(nbtTagCompound);
   }

   public Object getGUI(EntityPlayer player) {
      return null;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      if (this.destroyed) {
         return false;
      } else {
         return this.getPos().distanceSq(player.getPosition()) < 16.0D;
      }
   }

   public int[] getSlotsForFace(EnumFacing side) {
      return new int[0];
   }

   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return false;
   }

   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return false;
   }
}
