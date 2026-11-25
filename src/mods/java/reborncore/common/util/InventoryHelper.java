package reborncore.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.api.tile.IUpgradeable;
import reborncore.common.recipes.RecipeCrafter;

public class InventoryHelper {
   public InventoryHelper() {
   }

   public static void tryInsertStack(IInventory targetInventory, int slot, ItemStack stack, boolean canMerge) {
      if (targetInventory.isItemValidForSlot(slot, stack)) {
         ItemStack targetStack = targetInventory.getStackInSlot(slot);
         if (targetStack.isEmpty()) {
            int space = targetInventory.getInventoryStackLimit();
            int mergeAmount = Math.min(space, stack.getCount());
            ItemStack copy = stack.copy();
            copy.setCount(mergeAmount);
            targetInventory.setInventorySlotContents(slot, copy);
            stack.shrink(mergeAmount);
         } else if (canMerge && targetInventory.isItemValidForSlot(slot, stack) && areMergeCandidates(stack, targetStack)) {
            int space = Math.min(targetInventory.getInventoryStackLimit(), targetStack.getMaxStackSize()) - targetStack.getCount();
            int mergeAmount = Math.min(space, stack.getCount());
            ItemStack copy = targetStack.copy();
            copy.grow(mergeAmount);
            targetInventory.setInventorySlotContents(slot, copy);
            stack.shrink(mergeAmount);
         }
      }

   }

   protected static boolean areMergeCandidates(ItemStack source, ItemStack target) {
      return source.isItemEqual(target) && ItemStack.areItemStackTagsEqual(source, target) && target.getCount() < target.getMaxStackSize();
   }

   public static void insertItemIntoInventory(IInventory inventory, ItemStack stack) {
      insertItemIntoInventory(inventory, stack, (EnumFacing)null, -1);
   }

   public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot) {
      insertItemIntoInventory(inventory, stack, side, intoSlot, true);
   }

   public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot, boolean doMove) {
      insertItemIntoInventory(inventory, stack, side, intoSlot, doMove, true);
   }

   public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot, boolean doMove, boolean canStack) {
      if (!stack.isEmpty()) {
         IInventory targetInventory = inventory;
         if (!doMove) {
            targetInventory = new InventoryHelper.GenericInventory("temporary.inventory", false, inventory.getSizeInventory());
            ((InventoryHelper.GenericInventory)targetInventory).copyFrom(inventory);
         }

         int i = 0;
         int[] attemptSlots = new int[0];
         if (inventory instanceof ISidedInventory && side != null) {
            attemptSlots = ((ISidedInventory)inventory).getSlotsForFace(side);
            if (attemptSlots == null) {
               attemptSlots = new int[0];
            }
         } else {
            attemptSlots = new int[inventory.getSizeInventory()];

            for(int a = 0; a < inventory.getSizeInventory(); attemptSlots[a] = a++) {
               ;
            }
         }

         if (intoSlot > -1) {
            Set<Integer> x = new HashSet();

            for(int attemptedSlot : attemptSlots) {
               x.add(Integer.valueOf(attemptedSlot));
            }

            if (x.contains(Integer.valueOf(intoSlot))) {
               attemptSlots = new int[]{intoSlot};
            } else {
               attemptSlots = new int[0];
            }
         }

         while(stack.getCount() > 0 && i < attemptSlots.length) {
            if (side != null && inventory instanceof ISidedInventory && !((ISidedInventory)inventory).canInsertItem(attemptSlots[i], stack, side.getOpposite())) {
               ++i;
            } else {
               tryInsertStack(targetInventory, attemptSlots[i], stack, canStack);
               ++i;
            }
         }

      }
   }

   public static int testInventoryInsertion(IInventory inventory, ItemStack item, EnumFacing side) {
      if (!item.isEmpty() && item.getCount() != 0) {
         item = item.copy();
         if (inventory == null) {
            return 0;
         } else {
            inventory.getSizeInventory();
            int itemSizeCounter = item.getCount();
            int[] availableSlots = new int[0];
            if (inventory instanceof ISidedInventory && side != null) {
               availableSlots = ((ISidedInventory)inventory).getSlotsForFace(side);
            } else if (inventory instanceof IRecipeCrafterProvider) {
               RecipeCrafter recipeCrafter = ((IRecipeCrafterProvider)inventory).getRecipeCrafter();
               availableSlots = recipeCrafter.inputSlots;
            } else {
               availableSlots = buildSlotsForLinearInventory(inventory);
            }

            for(int i : availableSlots) {
               if (itemSizeCounter <= 0) {
                  break;
               }

               if (inventory.isItemValidForSlot(i, item) && (side == null || !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canInsertItem(i, item, side.getOpposite()))) {
                  ItemStack inventorySlot = inventory.getStackInSlot(i);
                  if (inventorySlot.isEmpty()) {
                     itemSizeCounter -= Math.min(Math.min(itemSizeCounter, inventory.getInventoryStackLimit()), item.getMaxStackSize());
                  } else if (areMergeCandidates(item, inventorySlot)) {
                     int space = Math.min(inventory.getInventoryStackLimit(), inventorySlot.getMaxStackSize()) - inventorySlot.getCount();
                     itemSizeCounter -= Math.min(itemSizeCounter, space);
                  }
               }
            }

            if (itemSizeCounter != item.getCount()) {
               itemSizeCounter = Math.max(itemSizeCounter, 0);
               return item.getCount() - itemSizeCounter;
            } else {
               return 0;
            }
         }
      } else {
         return 0;
      }
   }

   public static IInventory getInventory(World world, int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof TileEntityChest) {
         TileEntityChest chest = (TileEntityChest)tileEntity;
         TileEntityChest adjacent = null;
         if (chest.adjacentChestXNeg != null) {
            adjacent = chest.adjacentChestXNeg;
         }

         if (chest.adjacentChestXPos != null) {
            adjacent = chest.adjacentChestXPos;
         }

         if (chest.adjacentChestZNeg != null) {
            adjacent = chest.adjacentChestZNeg;
         }

         if (chest.adjacentChestZPos != null) {
            adjacent = chest.adjacentChestZPos;
         }

         return (IInventory)(adjacent != null ? new InventoryLargeChest("", chest, adjacent) : chest);
      } else {
         return tileEntity instanceof IInventory ? (IInventory)tileEntity : null;
      }
   }

   public static IInventory getInventory(World world, int x, int y, int z, EnumFacing direction) {
      if (direction != null) {
         x += direction.getFrontOffsetX();
         y += direction.getFrontOffsetY();
         z += direction.getFrontOffsetZ();
      }

      return getInventory(world, x, y, z);
   }

   public static IInventory getInventory(IInventory inventory) {
      if (inventory instanceof TileEntityChest) {
         TileEntity te = (TileEntity)inventory;
         return getInventory(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
      } else {
         return inventory;
      }
   }

   public static int[] buildSlotsForLinearInventory(IInventory inv) {
      int[] slots = new int[inv.getSizeInventory()];

      for(int i = 0; i < slots.length; slots[i] = i++) {
         ;
      }

      return slots;
   }

   public static void dropInventoryItems(World world, BlockPos pos) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity != null) {
         if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory)tileEntity;

            for(int i = 0; i < inventory.getSizeInventory(); ++i) {
               ItemStack itemStack = inventory.getStackInSlot(i);
               if (!itemStack.isEmpty() && (itemStack.getCount() <= 0 || !(itemStack.getItem() instanceof ItemBlock) || !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockFluidBase) && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockStaticLiquid) && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockDynamicLiquid))) {
                  net.minecraft.inventory.InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
               }
            }
         }

         if (tileEntity instanceof IUpgradeable) {
            net.minecraft.inventory.InventoryHelper.dropInventoryItems(world, pos, ((IUpgradeable)tileEntity).getUpgradeInventory());
         }

      }
   }

   public static class GenericInventory implements IInventory {
      protected String inventoryTitle;
      protected int slotsCount;
      protected ItemStack[] inventoryContents;
      protected boolean isInvNameLocalized;

      public GenericInventory(String name, boolean isInvNameLocalized, int size) {
         this.isInvNameLocalized = isInvNameLocalized;
         this.slotsCount = size;
         this.inventoryTitle = name;
         this.inventoryContents = new ItemStack[size];
      }

      public ItemStack decrStackSize(int par1, int par2) {
         if (!this.inventoryContents[par1].isEmpty()) {
            if (this.inventoryContents[par1].getCount() <= par2) {
               ItemStack itemstack = this.inventoryContents[par1];
               this.inventoryContents[par1] = ItemStack.EMPTY;
               return itemstack;
            } else {
               ItemStack itemstack = this.inventoryContents[par1].splitStack(par2);
               if (this.inventoryContents[par1].getCount() == 0) {
                  this.inventoryContents[par1] = ItemStack.EMPTY;
               }

               return itemstack;
            }
         } else {
            return ItemStack.EMPTY;
         }
      }

      public int getInventoryStackLimit() {
         return 64;
      }

      public int getSizeInventory() {
         return this.slotsCount;
      }

      public boolean isEmpty() {
         for(ItemStack itemstack : this.inventoryContents) {
            if (!itemstack.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public ItemStack getStackInSlot(int i) {
         return this.inventoryContents[i];
      }

      public ItemStack getStackInSlot(Enum<?> i) {
         return this.getStackInSlot(i.ordinal());
      }

      public ItemStack removeStackFromSlot(int i) {
         if (i >= this.inventoryContents.length) {
            return ItemStack.EMPTY;
         } else if (!this.inventoryContents[i].isEmpty()) {
            ItemStack itemstack = this.inventoryContents[i];
            this.inventoryContents[i] = ItemStack.EMPTY;
            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public boolean isItem(int slot, Item item) {
         return !this.inventoryContents[slot].isEmpty() && this.inventoryContents[slot].getItem() == item;
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

      public boolean isUsableByPlayer(EntityPlayer entityplayer) {
         return true;
      }

      public void openInventory(EntityPlayer player) {
      }

      public void closeInventory(EntityPlayer player) {
      }

      public void clearAndSetSlotCount(int amount) {
         this.slotsCount = amount;
         this.inventoryContents = new ItemStack[amount];
      }

      public void readFromNBT(NBTTagCompound tag) {
         if (tag.hasKey("size")) {
            this.slotsCount = tag.getInteger("size");
         }

         NBTTagList nbttaglist = tag.getTagList("Items", 10);
         this.inventoryContents = new ItemStack[this.slotsCount];

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
            int j = stacktag.getByte("Slot");
            if (j >= 0 && j < this.inventoryContents.length) {
               this.inventoryContents[j] = new ItemStack(stacktag);
            }
         }

      }

      public void setInventorySlotContents(int i, ItemStack itemstack) {
         this.inventoryContents[i] = itemstack;
         if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
            itemstack.setCount(this.getInventoryStackLimit());
         }

      }

      public void writeToNBT(NBTTagCompound tag) {
         tag.setInteger("size", this.getSizeInventory());
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < this.inventoryContents.length; ++i) {
            if (!this.inventoryContents[i].isEmpty()) {
               NBTTagCompound stacktag = new NBTTagCompound();
               stacktag.setByte("Slot", (byte)i);
               this.inventoryContents[i].writeToNBT(stacktag);
               nbttaglist.appendTag(stacktag);
            }
         }

         tag.setTag("Items", nbttaglist);
      }

      public void copyFrom(IInventory inventory) {
         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            if (i < this.getSizeInventory()) {
               ItemStack stack = inventory.getStackInSlot(i);
               if (!stack.isEmpty()) {
                  this.setInventorySlotContents(i, stack.copy());
               } else {
                  this.setInventorySlotContents(i, ItemStack.EMPTY);
               }
            }
         }

      }

      public List<ItemStack> contents() {
         return Arrays.asList(this.inventoryContents);
      }

      public void markDirty() {
      }

      public String getName() {
         return null;
      }

      public boolean hasCustomName() {
         return false;
      }

      public ITextComponent getDisplayName() {
         return null;
      }
   }
}
