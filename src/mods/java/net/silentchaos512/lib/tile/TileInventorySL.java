package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public abstract class TileInventorySL extends TileEntitySL implements IInventorySL {
   protected NonNullList<ItemStack> inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

   public TileInventorySL() {
   }

   public ITextComponent getDisplayName() {
      String name = this.getName();
      return name == null ? null : new TextComponentString(name);
   }

   public String getName() {
      return null;
   }

   public boolean hasCustomName() {
      return false;
   }

   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventory.size() ? (ItemStack)this.inventory.get(index) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.inventory, index, count);
   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.inventory, index);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventory.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsable(EntityPlayer player) {
      return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos) <= 64.0D;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.inventory.clear();
   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(tags, this.inventory);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      super.writeToNBT(tags);
      ItemStackHelper.saveAllItems(tags, this.inventory);
      return tags;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tags = this.getUpdateTag();
      ItemStackHelper.saveAllItems(tags, this.inventory);
      return new SPacketUpdateTileEntity(this.pos, 1, tags);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity packet) {
      super.onDataPacket(net, packet);
      ItemStackHelper.loadAllItems(packet.getNbtCompound(), this.inventory);
   }
}
