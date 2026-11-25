package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.EquipmentForgeScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;

public class TileEntityEquipmentForge extends TileEntityBase {
   protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(ItemEquipment.PART_LIMIT + 1, ItemStack.EMPTY);
   protected int level = 1;

   public TileEntityEquipmentForge() {
   }

   public void onRemove() {
      for(ItemStack itemStack : this.itemStacks) {
         if (!(itemStack.getItem() instanceof ItemEquipment)) {
            EntityItemCustom entityItem = new EntityItemCustom(this.getWorld(), (double)this.getPos().getX(), (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ(), itemStack);
            this.getWorld().spawnEntity(entityItem);
         }
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
      if (!(itemStack.getItem() instanceof ItemEquipment) && !(itemStack.getItem() instanceof ItemEquipmentPart)) {
         return false;
      } else {
         ItemStack existingStack = this.getStackInSlot(index);
         return existingStack.isEmpty();
      }
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

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound syncData = new NBTTagCompound();
      if (!this.getWorld().isRemote) {
         syncData.setInteger("ForgeLevel", this.level);
      }

      return new SPacketUpdateTileEntity(this.getPos(), 1, syncData);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      if (this.getWorld().isRemote) {
         NBTTagCompound syncData = packet.getNbtCompound();
         if (syncData.hasKey("ForgeLevel")) {
            this.level = syncData.getInteger("ForgeLevel");
         }

      }
   }

   public void onGuiButton(byte buttonId) {
      LycanitesMobs.logDebug("", "Received button packet id: " + buttonId);
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("ForgeLevel")) {
         this.level = nbtTagCompound.getInteger("ForgeLevel");
      }

      if (nbtTagCompound.hasKey("Items")) {
         ItemStackHelper.loadAllItems(nbtTagCompound, this.itemStacks);
      }

      super.readFromNBT(nbtTagCompound);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setInteger("ForgeLevel", this.level);
      ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);
      return super.writeToNBT(nbtTagCompound);
   }

   public Object getGUI(EntityPlayer player) {
      if (player.world.isRemote) {
         return new EquipmentForgeScreen(this, player.inventory);
      } else {
         if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).connection.sendPacket(this.getUpdatePacket());
         }

         return new EquipmentForgeContainer(this, player.inventory);
      }
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public String getName() {
      String levelName = "lesser";
      if (this.level == 2) {
         levelName = "greater";
      } else if (this.level >= 3) {
         levelName = "master";
      }

      return LanguageManager.translate("tile.equipmentforge_" + levelName + ".name");
   }

   public boolean hasCustomName() {
      return false;
   }
}
