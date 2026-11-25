package net.theawesomegem.fishingmadebetter.common.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom.Item;

public class WeightedRandomItemStack extends Item {
   private ItemStack itemStack;

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public WeightedRandomItemStack(ItemStack itemStack, int itemWeightIn) {
      super(itemWeightIn);
      this.itemStack = itemStack;
   }
}
