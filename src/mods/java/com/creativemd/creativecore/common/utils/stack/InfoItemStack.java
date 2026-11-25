package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InfoItemStack extends InfoStack {
   public ItemStack stack;
   public boolean needNBT;

   public InfoItemStack(ItemStack stack, boolean needNBT, int stackSize) {
      super(stackSize);
      this.stack = stack;
      this.needNBT = needNBT;
   }

   public InfoItemStack(ItemStack stack, int stackSize) {
      this(stack, stack.hasTagCompound(), stackSize);
   }

   public InfoItemStack(ItemStack stack) {
      this(stack, stack.getCount());
   }

   public InfoItemStack() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      nbt.setTag("stack", this.stack.writeToNBT(new NBTTagCompound()));
      nbt.setBoolean("needNBT", this.needNBT);
   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      this.stack = new ItemStack(nbt.getCompoundTag("stack"));
      this.needNBT = nbt.getBoolean("needNBT");
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      if (info instanceof InfoItemStack) {
         if (((InfoItemStack)info).stack.getItem() != this.stack.getItem()) {
            return false;
         } else if (((InfoItemStack)info).stack.getMetadata() != this.stack.getMetadata()) {
            return false;
         } else if (!((InfoItemStack)info).needNBT && !this.needNBT) {
            return true;
         } else if (((InfoItemStack)info).stack.getTagCompound() == null && this.stack.getTagCompound() == null) {
            return true;
         } else {
            return ((InfoItemStack)info).stack.getTagCompound() != null && this.stack.getTagCompound() != null ? ((InfoItemStack)info).stack.getTagCompound().equals(this.stack.getTagCompound()) : false;
         }
      } else {
         return false;
      }
   }

   public InfoStack copy() {
      return new InfoItemStack(this.stack.copy(), this.needNBT, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      ItemStack stack = new ItemStack(this.stack.getItem(), stacksize, this.stack.getMetadata());
      if (this.needNBT && this.stack.hasTagCompound()) {
         stack.setTagCompound(this.stack.getTagCompound().copy());
      }

      return stack;
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      if (stack.getItem() != this.stack.getItem()) {
         return false;
      } else if (stack.getMetadata() != this.stack.getMetadata()) {
         return false;
      } else if (this.needNBT) {
         if (stack.getTagCompound() == this.stack.getTagCompound()) {
            return true;
         } else {
            return stack.getTagCompound() != null && this.stack.getTagCompound() != null ? stack.getTagCompound().equals(this.stack.getTagCompound()) : false;
         }
      } else {
         return true;
      }
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoItemStack && ((InfoItemStack)object).isInstanceIgnoreSize(this.stack);
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      ArrayList<ItemStack> stacks = new ArrayList();
      stacks.add(this.getItemStack(1));
      return stacks;
   }
}
