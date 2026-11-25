package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class InfoOre extends InfoStack {
   public String ore;

   public InfoOre(String ore, int stackSize) {
      super(stackSize);
      this.ore = ore;
   }

   public InfoOre(String ore) {
      this(ore, 1);
   }

   public InfoOre() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      nbt.setString("ore", this.ore);
   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      this.ore = nbt.getString("ore");
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      return info instanceof InfoOre ? ((InfoOre)info).ore.equals(this.ore) : false;
   }

   public InfoStack copy() {
      return new InfoOre(this.ore, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      List<ItemStack> stacks = OreDictionary.getOres(this.ore);
      if (stacks.size() > 0) {
         ItemStack stack = ((ItemStack)stacks.get(0)).copy();
         if (stack.getMetadata() == 32767) {
            stack.setItemDamage(0);
         }

         stack.setCount(stacksize);
         return stack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      int[] ores = OreDictionary.getOreIDs(stack);

      for(int i = 0; i < ores.length; ++i) {
         if (OreDictionary.getOreName(ores[i]).equals(this.ore)) {
            return true;
         }
      }

      return false;
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoOre && ((InfoOre)object).ore.equals(this.ore);
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      return new ArrayList(OreDictionary.getOres(this.ore));
   }
}
