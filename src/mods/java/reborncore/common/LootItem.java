package reborncore.common;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LootItem {
   ItemStack item;
   double chance;
   int minSize;
   int maxSize;
   ResourceLocation lootTableList;

   public LootItem(ItemStack item, double chance, ResourceLocation lootTableList) {
      this(item, chance, 1, 1, lootTableList);
   }

   public LootItem(ItemStack item, double chance, int minSize, int maxSize, ResourceLocation lootTableList) {
      this.item = item;
      this.chance = chance;
      this.minSize = minSize;
      this.maxSize = maxSize;
      this.lootTableList = lootTableList;
   }

   public ItemStack createStack(EaglercraftRandom rnd) {
      int size = this.minSize;
      if (this.maxSize > this.minSize) {
         size += rnd.nextInt(this.maxSize - this.minSize + 1);
      }

      ItemStack result = this.item.copy();
      result.setCount(size);
      return result;
   }

   public ResourceLocation getLootTableList() {
      return this.lootTableList;
   }
}
