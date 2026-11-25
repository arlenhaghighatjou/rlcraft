package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

public class InfoFuel extends InfoStack {
   public InfoFuel(int stackSize) {
      super(stackSize);
   }

   public InfoFuel() {
      super(1);
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      return TileEntityFurnace.isItemFuel(info.getItemStack());
   }

   public InfoStack copy() {
      return new InfoFuel(this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      return new ItemStack(Items.COAL);
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      return TileEntityFurnace.isItemFuel(stack);
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoFuel;
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
