package slimeknights.mantle.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import slimeknights.mantle.util.ItemStackList;

public class TileInventory extends MantleTileEntity implements IInventory {
   private NonNullList<ItemStack> inventory;
   protected String inventoryTitle;
   protected boolean hasCustomName;
   protected int stackSizeLimit;
   protected IItemHandlerModifiable itemHandler;

   public TileInventory(String name, int inventorySize) {
      this(name, inventorySize, 64);
   }

   public TileInventory(String name, int inventorySize, int maxStackSize) {
      this.inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
      this.stackSizeLimit = maxStackSize;
      this.inventoryTitle = name;
      this.itemHandler = new InvWrapper(this);
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.itemHandler : super.getCapability(capability, facing));
   }

   public IItemHandlerModifiable getItemHandler() {
      return this.itemHandler;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return slot >= 0 && slot < this.inventory.size() ? (ItemStack)this.inventory.get(slot) : ItemStack.EMPTY;
   }

   public boolean isStackInSlot(int slot) {
      return !this.getStackInSlot(slot).isEmpty();
   }

   private void resizeInternal(int size) {
      if (size != this.inventory.size()) {
         ItemStackList newInventory = ItemStackList.withSize(size);

         for(int i = 0; i < size && i < this.inventory.size(); ++i) {
            newInventory.set(i, this.inventory.get(i));
         }

         this.inventory = newInventory;
      }
   }

   public void resize(int size) {
      this.resizeInternal(size);
      this.markDirtyFast();
   }

   public int getSizeInventory() {
      return this.inventory.size();
   }

   public int getInventoryStackLimit() {
      return this.stackSizeLimit;
   }

   public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
      if (slot >= 0 && slot < this.inventory.size()) {
         ItemStack current = (ItemStack)this.inventory.get(slot);
         this.inventory.set(slot, itemstack);
         if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
            itemstack.setCount(this.getInventoryStackLimit());
         }

         if (!ItemStack.areItemStacksEqual(current, itemstack)) {
            this.markDirtyFast();
         }

      }
   }

   @Nonnull
   public ItemStack decrStackSize(int slot, int quantity) {
      if (quantity <= 0) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = this.getStackInSlot(slot);
         if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (itemStack.getCount() <= quantity) {
            this.setInventorySlotContents(slot, ItemStack.EMPTY);
            this.markDirtyFast();
            return itemStack;
         } else {
            itemStack = itemStack.splitStack(quantity);
            if (this.getStackInSlot(slot).getCount() == 0) {
               this.setInventorySlotContents(slot, ItemStack.EMPTY);
            }

            this.markDirtyFast();
            return itemStack;
         }
      }
   }

   @Nonnull
   public ItemStack removeStackFromSlot(int slot) {
      ItemStack itemStack = this.getStackInSlot(slot);
      this.setInventorySlotContents(slot, ItemStack.EMPTY);
      return itemStack;
   }

   public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
      return slot < this.getSizeInventory() && (((ItemStack)this.inventory.get(slot)).isEmpty() || itemstack.getCount() + ((ItemStack)this.inventory.get(slot)).getCount() <= this.getInventoryStackLimit());
   }

   public void clear() {
      for(int i = 0; i < this.inventory.size(); ++i) {
         this.inventory.set(i, ItemStack.EMPTY);
      }

   }

   @Nonnull
   public String getName() {
      return this.inventoryTitle;
   }

   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String customName) {
      this.hasCustomName = true;
      this.inventoryTitle = customName;
   }

   @Nonnull
   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
   }

   public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
      if (this.world.getTileEntity(this.pos) == this && this.world.getBlockState(this.pos).getBlock() != Blocks.AIR) {
         return entityplayer.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      } else {
         return false;
      }
   }

   public void openInventory(@Nonnull EntityPlayer player) {
   }

   public void closeInventory(@Nonnull EntityPlayer player) {
   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      this.resizeInternal(tags.getInteger("InventorySize"));
      this.readInventoryFromNBT(tags);
      if (tags.hasKey("CustomName", 8)) {
         this.inventoryTitle = tags.getString("CustomName");
      }

   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      super.writeToNBT(tags);
      tags.setInteger("InventorySize", this.inventory.size());
      this.writeInventoryToNBT(tags);
      if (this.hasCustomName()) {
         tags.setString("CustomName", this.inventoryTitle);
      }

      return tags;
   }

   public void writeInventoryToNBT(NBTTagCompound tag) {
      IInventory inventory = this;
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         if (!inventory.getStackInSlot(i).isEmpty()) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte)i);
            inventory.getStackInSlot(i).writeToNBT(itemTag);
            nbttaglist.appendTag(itemTag);
         }
      }

      tag.setTag("Items", nbttaglist);
   }

   public void readInventoryFromNBT(NBTTagCompound tag) {
      NBTTagList nbttaglist = tag.getTagList("Items", 10);
      int limit = this.getInventoryStackLimit();

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound itemTag = nbttaglist.getCompoundTagAt(i);
         int slot = itemTag.getByte("Slot") & 255;
         if (slot >= 0 && slot < this.inventory.size()) {
            ItemStack stack = new ItemStack(itemTag);
            if (!stack.isEmpty() && stack.getCount() > limit) {
               stack.setCount(limit);
            }

            this.inventory.set(slot, stack);
         }
      }

   }

   @Nonnull
   public ItemStack getStackInSlotOnClosing(int slot) {
      return ItemStack.EMPTY;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.inventory) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
