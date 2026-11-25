package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class InfoItem extends InfoStack {
   public Item item;

   public InfoItem(Item item, int stackSize) {
      super(stackSize);
      this.item = item;
   }

   public InfoItem(Item item) {
      this(item, 1);
   }

   public InfoItem() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      Item var10000 = this.item;
      ResourceLocation location = (ResourceLocation)Item.REGISTRY.getNameForObject(this.item);
      if (location != null) {
         nbt.setString("item", location.toString());
      } else {
         System.out.println("Invalid InfoBlock entry. Item does not exists");
      }

   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      this.item = (Item)Item.REGISTRY.getObject(new ResourceLocation(nbt.getString("item")));
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      if (info instanceof InfoItem) {
         return ((InfoItem)info).item == this.item;
      } else if (info instanceof InfoItemStack) {
         return this.item == ((InfoItemStack)info).stack.getItem();
      } else {
         return false;
      }
   }

   public InfoStack copy() {
      return new InfoItem(this.item, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      return new ItemStack(this.item, stacksize);
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      return stack.getItem() == this.item;
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoItem && ((InfoItem)object).item == this.item;
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      ArrayList<ItemStack> stacks = new ArrayList();
      stacks.add(new ItemStack(this.item, 1, 32767));
      return stacks;
   }
}
