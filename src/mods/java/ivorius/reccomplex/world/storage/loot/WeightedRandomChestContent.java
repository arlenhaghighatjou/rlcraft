package ivorius.reccomplex.world.storage.loot;

import net.minecraft.item.ItemStack;

public class WeightedRandomChestContent {
   public ItemStack theItemId;
   public int minStackSize;
   public int maxStackSize;
   public int itemWeight;

   public WeightedRandomChestContent(ItemStack theItemId, int minStackSize, int maxStackSize, int itemWeight) {
      this.theItemId = theItemId;
      this.minStackSize = minStackSize;
      this.maxStackSize = maxStackSize;
      this.itemWeight = itemWeight;
   }
}
