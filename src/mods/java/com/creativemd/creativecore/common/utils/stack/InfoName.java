package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class InfoName extends InfoStack {
   public String name;

   public InfoName(String name, int stackSize) {
      super(stackSize);
      this.name = name;
   }

   public InfoName(String name) {
      this(name, 1);
   }

   public InfoName() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      nbt.setString("name", this.name);
   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      this.name = nbt.getString("name");
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      return info instanceof InfoName ? ((InfoName)info).name.contains(this.name) : false;
   }

   public InfoStack copy() {
      return new InfoName(this.name, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      for(ResourceLocation location : Block.REGISTRY.getKeys()) {
         if (location.toString().contains(this.name)) {
            return new ItemStack((Block)Block.REGISTRY.getObject(location), this.stackSize);
         }
      }

      for(ResourceLocation location : Item.REGISTRY.getKeys()) {
         if (location.toString().contains(this.name)) {
            return new ItemStack((Item)Item.REGISTRY.getObject(location), this.stackSize);
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      if (stack.getItem() instanceof ItemBlock) {
         return ((ResourceLocation)Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem()))).toString().contains(this.name);
      } else {
         ResourceLocation item = (ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem());
         return item != null ? item.toString().contains(this.name) : false;
      }
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoName && ((InfoName)object).name.equals(this.name);
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      ArrayList<ItemStack> result = new ArrayList();
      List<ItemStack> stacks = getAllExistingItems();

      for(int i = 0; i < stacks.size(); ++i) {
         if (this.isInstanceIgnoreSize((ItemStack)stacks.get(i))) {
            result.add(stacks.get(i));
         }
      }

      return result;
   }
}
