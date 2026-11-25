package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.github.alexthe666.iceandfire.message.MessageUpdatePodium;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityPodium extends TileEntity implements ITickable, ISidedInventory {
   private static final int[] slotsTop = new int[]{0};
   private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

   public TileEntityPodium() {
   }

   public void update() {
   }

   public int getSizeInventory() {
      return this.stacks.size();
   }

   public ItemStack getStackInSlot(int index) {
      return (ItemStack)this.stacks.get(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      if (!((ItemStack)this.stacks.get(index)).isEmpty()) {
         if (((ItemStack)this.stacks.get(index)).getCount() <= count) {
            ItemStack itemstack = (ItemStack)this.stacks.get(index);
            this.stacks.set(index, ItemStack.EMPTY);
            return itemstack;
         } else {
            ItemStack itemstack = ((ItemStack)this.stacks.get(index)).splitStack(count);
            if (((ItemStack)this.stacks.get(index)).isEmpty()) {
               this.stacks.set(index, ItemStack.EMPTY);
            }

            return itemstack;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getStackInSlotOnClosing(int index) {
      if (!((ItemStack)this.stacks.get(index)).isEmpty()) {
         ItemStack itemstack = (ItemStack)this.stacks.get(index);
         this.stacks.set(index, itemstack);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      if (!stack.isEmpty() && stack.isItemEqual((ItemStack)this.stacks.get(index)) && ItemStack.areItemStackTagsEqual(stack, (ItemStack)this.stacks.get(index))) {
         boolean var4 = true;
      } else {
         boolean var10000 = false;
      }

      this.stacks.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.stacks);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      ItemStackHelper.saveAllItems(compound, this.stacks);
      return compound;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
      return index != 0 || stack.getItem() instanceof ItemDragonEgg || stack.getItem() instanceof ItemMyrmexEgg;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 4;
   }

   public void clear() {
      this.stacks.clear();
   }

   public String getName() {
      return "tile.iceandfire.podium.name";
   }

   public int[] getSlotsForFace(EnumFacing side) {
      return slotsTop;
   }

   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return false;
   }

   public boolean hasCustomName() {
      return false;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return false;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.pos, 1, tag);
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
      if (!this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePodium(this.pos.toLong(), this.getStackInSlot(0)));
      }

   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStack.EMPTY;
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         if (!this.getStackInSlot(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
