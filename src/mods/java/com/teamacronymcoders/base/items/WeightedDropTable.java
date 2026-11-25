package com.teamacronymcoders.base.items;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;

public class WeightedDropTable implements IDropTable {
   private ItemStack[][] dropTable;
   private Boolean[] fortuneTable;
   private Random rng = new Random();

   public WeightedDropTable(List<List<ItemStack>> dropTable, List<Boolean> fortuneTable) {
      this.dropTable = new ItemStack[dropTable.size()][];

      for(int slot = 0; slot < dropTable.size(); ++slot) {
         this.dropTable[slot] = (ItemStack[])((List)dropTable.get(slot)).toArray(new ItemStack[0]);
      }

      this.fortuneTable = (Boolean[])fortuneTable.toArray(new Boolean[0]);
   }

   public List<ItemStack> getDrops(int fortune) {
      List<ItemStack> itemsToDrop = new LinkedList();

      for(int slot = 0; slot < this.dropTable.length; ++slot) {
         int count;
         if (this.fortuneTable[slot].booleanValue()) {
            count = Math.max(this.rng.nextInt(fortune + 2), 1);
         } else {
            count = 1;
         }

         for(int i = 0; i < count; ++i) {
            ItemStack drop = this.dropTable[slot][this.rng.nextInt(this.dropTable[slot].length)];
            itemsToDrop.add(drop.copy());
         }
      }

      return itemsToDrop;
   }
}
