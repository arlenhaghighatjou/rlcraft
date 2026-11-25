package reborncore.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Inventory implements ISidedInventory {
   public ItemStack[] contents;
   private final String name;
   private final int stackLimit;
   private TileEntity tile;
   public boolean hasChanged = false;
   public boolean isDirty = false;

   public Inventory(int size, String invName, int invStackLimit, TileEntity tileEntity) {
      this.contents = new ItemStack[size];

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.contents[i] = ItemStack.EMPTY;
      }

      this.name = invName;
      this.stackLimit = invStackLimit == 64 ? Items.AIR.getItemStackLimit() : invStackLimit;
      this.tile = tileEntity;
   }

   public int getSizeInventory() {
      return this.contents.length;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.contents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int slotId) {
      return this.contents[slotId];
   }

   public ItemStack decrStackSize(int slotId, int count) {
      if (slotId < this.contents.length && !this.contents[slotId].isEmpty()) {
         if (this.contents[slotId].getCount() > count) {
            ItemStack result = this.contents[slotId].splitStack(count);
            this.markDirty();
            this.hasChanged = true;
            return result;
         } else {
            ItemStack stack = this.contents[slotId];
            this.setInventorySlotContents(slotId, ItemStack.EMPTY);
            this.hasChanged = true;
            return stack;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int slotId, ItemStack itemstack) {
      if (slotId < this.contents.length) {
         this.contents[slotId] = itemstack;
         if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
            itemstack.setCount(this.getInventoryStackLimit());
         }

         this.markDirty();
         this.hasChanged = true;
      }
   }

   public int getInventoryStackLimit() {
      return this.stackLimit;
   }

   public boolean isUsableByPlayer(EntityPlayer entityplayer) {
      return true;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public void readFromNBT(NBTTagCompound data) {
      this.readFromNBT(data, "Items");
   }

   public void readFromNBT(NBTTagCompound data, String tag) {
      NBTTagList nbttaglist = data.getTagList(tag, 10);

      for(int j = 0; j < nbttaglist.tagCount(); ++j) {
         NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
         int index;
         if (slot.hasKey("index")) {
            index = slot.getInteger("index");
         } else {
            index = slot.getByte("Slot");
         }

         if (index >= 0 && index < this.contents.length) {
            this.setInventorySlotContents(index, new ItemStack(slot));
         }
      }

      this.hasChanged = true;
   }

   public void writeToNBT(NBTTagCompound data) {
      this.writeToNBT(data, "Items");
   }

   public void writeToNBT(NBTTagCompound data, String tag) {
      NBTTagList slots = new NBTTagList();

      for(byte index = 0; index < this.contents.length; ++index) {
         if (!this.contents[index].isEmpty() && this.contents[index].getCount() > 0) {
            NBTTagCompound slot = new NBTTagCompound();
            slots.appendTag(slot);
            slot.setByte("Slot", index);
            this.contents[index].writeToNBT(slot);
         }
      }

      data.setTag(tag, slots);
   }

   public void setTile(TileEntity tileEntity) {
      this.tile = tileEntity;
   }

   public ItemStack removeStackFromSlot(int slotId) {
      if (this.contents[slotId].isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackToTake = this.contents[slotId];
         this.setInventorySlotContents(slotId, ItemStack.EMPTY);
         this.hasChanged = true;
         return stackToTake;
      }
   }

   public ItemStack[] getStacks() {
      return this.contents;
   }

   public boolean isItemValidForSlot(int i, ItemStack itemstack) {
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
   }

   public void markDirty() {
      this.tile.markDirty();
   }

   public String getName() {
      return this.name;
   }

   public boolean hasCustomName() {
      return false;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString(this.name);
   }

   public TileEntity getTileBase() {
      return this.tile;
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
