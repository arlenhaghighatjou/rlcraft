package reborncore.common.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class OreDropSet {
   public OreDrop[] dropSet;

   public OreDropSet(OreDrop... oreDrops) {
      this.dropSet = oreDrops;
   }

   public NonNullList<ItemStack> drop(int fortune, EaglercraftRandom random) {
      NonNullList<ItemStack> drops = NonNullList.create();

      for(OreDrop drop : this.dropSet) {
         drops.add(drop.getDrops(fortune, random));
      }

      return drops;
   }
}
