package dev.satyrn.wolfarmor.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public abstract class OreDictHelper {
   public OreDictHelper() {
   }

   public static boolean isOre(boolean strict, String oreName, ItemStack... inputs) {
      List<ItemStack> matchedOreItems = OreDictionary.getOres(oreName);
      ItemStack[] dictTargets = (ItemStack[])matchedOreItems.toArray(new ItemStack[0]);
      NonNullList<ItemStack> inputList = NonNullList.create();
      inputList.addAll(Lists.newArrayList(inputs));
      return OreDictionary.containsMatch(strict, inputList, dictTargets);
   }
}
