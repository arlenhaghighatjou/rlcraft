package reborncore.common.util;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class OreDropSet {
   public OreDrop[] dropSet;

   public OreDropSet(OreDrop... oreDrops) {
      this.dropSet = oreDrops;
   }

   public NonNullList<ItemStack> drop(int fortune, Random random) {
      NonNullList<ItemStack> drops = NonNullList.create();

      for(OreDrop drop : this.dropSet) {
         drops.add(drop.getDrops(fortune, random));
      }

      return drops;
   }
}
