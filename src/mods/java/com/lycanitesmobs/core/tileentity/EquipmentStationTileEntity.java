package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.client.gui.EquipmentStationScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.EquipmentStationContainer;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class EquipmentStationTileEntity extends TileEntityBase {
   protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);

   public EquipmentStationTileEntity() {
   }

   public Object getGUI(EntityPlayer player) {
      return player.world.isRemote ? new EquipmentStationScreen(this, player.inventory) : new EquipmentStationContainer(this, player.inventory);
   }

   public String getName() {
      return LanguageManager.translate("tile.equipment_station");
   }

   public boolean hasCustomName() {
      return false;
   }

   public void onRemove() {
      for(ItemStack itemStack : this.itemStacks) {
         EntityItemCustom entityItem = new EntityItemCustom(this.getWorld(), (double)this.getPos().getX(), (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ(), itemStack);
         this.getWorld().spawnEntity(entityItem);
      }

      this.clear();
      super.onRemove();
   }

   public void update() {
      super.update();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.itemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      return (ItemStack)this.itemStacks.get(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.itemStacks, index, count);
   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.itemStacks, index);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.itemStacks.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public int getSizeInventory() {
      return this.itemStacks.size();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack itemStack) {
      ItemStack existingStack = this.getStackInSlot(index);
      return existingStack.isEmpty();
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
      this.itemStacks.clear();
   }

   public boolean receiveClientEvent(int eventID, int eventArg) {
      return false;
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("Items")) {
         ItemStackHelper.loadAllItems(nbtTagCompound, this.itemStacks);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);
      return super.writeToNBT(nbtTagCompound);
   }
}
