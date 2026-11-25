package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InfoContainOre extends InfoOre {
   public InfoContainOre(String name) {
      this(name, 1);
   }

   public InfoContainOre(String name, int stackSize) {
      super(name.toLowerCase(), stackSize);
   }

   public InfoContainOre() {
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      if (this.equalsIgnoreSize(info)) {
         return true;
      } else {
         return info instanceof InfoOre ? ((InfoOre)info).ore.toLowerCase().contains(this.ore) : false;
      }
   }

   public InfoStack copy() {
      return new InfoContainOre(this.ore, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      String[] names = OreDictionary.getOreNames();

      for(int i = 0; i < names.length; ++i) {
         if (names[i].toLowerCase().contains(this.ore)) {
            List<ItemStack> stacks = OreDictionary.getOres(names[i]);
            if (stacks.size() > 0) {
               ItemStack stack = ((ItemStack)stacks.get(0)).copy();
               if (stack.getMetadata() == 32767) {
                  stack.setItemDamage(0);
               }

               stack.setCount(stacksize);
               return stack;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      int[] ores = OreDictionary.getOreIDs(stack);

      for(int j = 0; j < ores.length; ++j) {
         if (OreDictionary.getOreName(ores[j]).toLowerCase().contains(this.ore)) {
            return true;
         }
      }

      return false;
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoContainOre && ((InfoContainOre)object).ore.equals(this.ore);
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
