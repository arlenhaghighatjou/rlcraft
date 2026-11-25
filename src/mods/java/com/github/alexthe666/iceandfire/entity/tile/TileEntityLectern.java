package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.inventory.ContainerLectern;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLectern extends TileEntity implements ITickable, ISidedInventory {
   private static final int[] slotsTop = new int[]{0};
   private static final int[] slotsBottom = new int[]{2, 1};
   private static final int[] slotsSides = new int[]{1};
   private static Random rand = new Random();
   public float pageFlip;
   public float pageFlipPrev;
   public float pageHelp1;
   public float pageHelp2;
   private NonNullList<ItemStack> stacks = NonNullList.withSize(3, ItemStack.EMPTY);
   private int furnaceBurnTime;
   private int currentItemBurnTime;
   private int cookTime;
   private int totalCookTime;
   private String furnaceCustomName;

   public TileEntityLectern() {
   }

   @SideOnly(Side.CLIENT)
   public static boolean isBurning(IInventory i) {
      return i.getField(0) > 0;
   }

   public static int getItemBurnTime(ItemStack i) {
      if (i.isEmpty()) {
         return 0;
      } else {
         Item item = i.getItem();
         return item == ModItems.manuscript ? 300 : 0;
      }
   }

   public static boolean isItemFuel(ItemStack i) {
      return getItemBurnTime(i) > 0;
   }

   public void update() {
      boolean flag = this.isBurning();
      boolean flag1 = false;
      if (this.isBurning()) {
         --this.furnaceBurnTime;
      }

      if (!this.world.isRemote) {
         if (this.isBurning() || !((ItemStack)this.stacks.get(1)).isEmpty() && !((ItemStack)this.stacks.get(0)).isEmpty()) {
            if (!this.isBurning() && this.canSmelt()) {
               this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime((ItemStack)this.stacks.get(1));
               if (this.isBurning()) {
                  flag1 = true;
                  if (!((ItemStack)this.stacks.get(1)).isEmpty()) {
                     ((ItemStack)this.stacks.get(1)).shrink(1);
                     if (((ItemStack)this.stacks.get(1)).isEmpty()) {
                        this.stacks.set(1, ((ItemStack)this.stacks.get(1)).getItem().getContainerItem((ItemStack)this.stacks.get(1)));
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt()) {
               ++this.cookTime;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = 300;
                  this.smeltItem();
                  flag1 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (flag != this.isBurning()) {
            flag1 = true;
         }
      }

      if (flag1) {
         this.markDirty();
      }

      float f1 = this.pageHelp1;

      while(true) {
         this.pageHelp1 += (float)(rand.nextInt(4) - rand.nextInt(4));
         if (f1 != this.pageHelp1) {
            break;
         }
      }

      this.pageFlipPrev = this.pageFlip;
      float f = (this.pageHelp1 - this.pageFlip) * 0.04F;
      float f3 = 0.02F;
      f = MathHelper.clamp(f, -f3, f3);
      this.pageHelp2 += (f - this.pageHelp2) * 0.9F;
      this.pageFlip += this.pageHelp2;
   }

   public int getSizeInventory() {
      return 3;
   }

   public ItemStack getStackInSlot(int index) {
      return (ItemStack)this.stacks.get(index);
   }

   private boolean canSmelt() {
      if (((ItemStack)this.stacks.get(0)).isEmpty()) {
         return false;
      } else {
         ItemStack itemstack = ((ItemStack)this.stacks.get(0)).copy();
         if (itemstack.isEmpty()) {
            return false;
         } else if (itemstack.getItem() != ModItems.bestiary) {
            return false;
         } else {
            if (itemstack.getItem() == ModItems.bestiary) {
               List list = EnumBestiaryPages.possiblePages(itemstack);
               if (list == null || list.isEmpty()) {
                  return false;
               }
            }

            if (((ItemStack)this.stacks.get(2)).isEmpty()) {
               return true;
            } else {
               int result = ((ItemStack)this.stacks.get(2)).getCount() + itemstack.getCount();
               return result <= this.getInventoryStackLimit() && result <= ((ItemStack)this.stacks.get(2)).getMaxStackSize();
            }
         }
      }
   }

   public ItemStack decrStackSize(int index, int count) {
      if (!((ItemStack)this.stacks.get(index)).isEmpty()) {
         if (((ItemStack)this.stacks.get(index)).getCount() <= count) {
            ItemStack itemstack = (ItemStack)this.stacks.get(index);
            this.stacks.set(index, ItemStack.EMPTY);
            return itemstack;
         } else {
            ItemStack itemstack = ((ItemStack)this.stacks.get(index)).splitStack(count);
            if (((ItemStack)this.stacks.get(index)).getCount() == 0) {
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
         this.stacks.set(index, ItemStack.EMPTY);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      boolean flag = !stack.isEmpty() && stack.isItemEqual((ItemStack)this.stacks.get(index)) && ItemStack.areItemStackTagsEqual(stack, (ItemStack)this.stacks.get(index));
      this.stacks.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      if (index == 0 && !flag) {
         this.totalCookTime = 300;
         this.cookTime = 0;
         this.markDirty();
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.stacks);
      this.furnaceBurnTime = compound.getShort("BurnTime");
      this.cookTime = compound.getShort("CookTime");
      this.totalCookTime = compound.getShort("CookTimeTotal");
      this.currentItemBurnTime = getItemBurnTime((ItemStack)this.stacks.get(1));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setShort("BurnTime", (short)this.furnaceBurnTime);
      compound.setShort("CookTime", (short)this.cookTime);
      compound.setShort("CookTimeTotal", (short)this.totalCookTime);
      ItemStackHelper.saveAllItems(compound, this.stacks);
      return compound;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public int getFieldCount() {
      return 4;
   }

   public void clear() {
      this.stacks.clear();
   }

   public String getName() {
      return "tile.iceandfire.lectern.name";
   }

   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return false;
   }

   public boolean hasCustomName() {
      return false;
   }

   public boolean isBurning() {
      return this.furnaceBurnTime > 0;
   }

   public void smeltItem() {
      if (this.canSmelt()) {
         ItemStack itemstack = ((ItemStack)this.stacks.get(0)).copy();
         if (((ItemStack)this.stacks.get(0)).getItem() == ModItems.bestiary) {
            EnumBestiaryPages.addRandomPage(itemstack);
         }

         ((ItemStack)this.stacks.get(0)).shrink(1);
         if (((ItemStack)this.stacks.get(0)).isEmpty()) {
            this.stacks.set(0, ItemStack.EMPTY);
         }

         if (((ItemStack)this.stacks.get(2)).isEmpty()) {
            this.stacks.set(2, itemstack);
         } else if (((ItemStack)this.stacks.get(2)).getItem() == itemstack.getItem()) {
            ((ItemStack)this.stacks.get(2)).grow(itemstack.getCount());
         }
      }

   }

   public int[] getSlotsForFace(EnumFacing side) {
      return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
   }

   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   public String getGuiID() {
      return "iceandfire:lectern";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerLectern(playerInventory, this);
   }

   public int getField(int id) {
      switch(id) {
      case 0:
         return this.furnaceBurnTime;
      case 1:
         return this.currentItemBurnTime;
      case 2:
         return this.cookTime;
      case 3:
         return this.totalCookTime;
      default:
         return 0;
      }
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return index == 2 ? false : (index != 1 ? true : isItemFuel(stack));
   }

   public void setField(int id, int value) {
      switch(id) {
      case 0:
         this.furnaceBurnTime = value;
         break;
      case 1:
         this.currentItemBurnTime = value;
         break;
      case 2:
         this.cookTime = value;
         break;
      case 3:
         this.totalCookTime = value;
      }

   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStack.EMPTY;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.pos, 1, tag);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
