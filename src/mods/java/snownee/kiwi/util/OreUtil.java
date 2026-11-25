package snownee.kiwi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import snownee.kiwi.KiwiConfig;
import snownee.kiwi.util.definition.ItemDefinition;

public final class OreUtil {
   public OreUtil() {
   }

   public static boolean doesItemHaveOreName(ItemStack stack, String ore) {
      if (!stack.isEmpty() && OreDictionary.doesOreNameExist(ore)) {
         int oreid = OreDictionary.getOreID(ore);

         for(int id : OreDictionary.getOreIDs(stack)) {
            if (oreid == id) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static List<String> getOreNames(ItemStack stack) {
      List<String> names = new ArrayList(4);
      if (!stack.isEmpty()) {
         for(int id : OreDictionary.getOreIDs(stack)) {
            names.add(OreDictionary.getOreName(id));
         }
      }

      return names;
   }

   public static ItemStack getPreferredItemFromOre(String ore) {
      ItemStack preferred = ItemStack.EMPTY;
      int i = Integer.MAX_VALUE;

      label36:
      for(ItemStack item : OreDictionary.getOres(ore, false)) {
         if (i == Integer.MAX_VALUE && preferred.isEmpty()) {
            preferred = item;
         }

         String modid = item.getItem().getCreatorModId(item);

         for(int j = 0; j <= Math.min(i, KiwiConfig.GENERAL.orePreference.length - 1); ++j) {
            if (modid.equals(KiwiConfig.GENERAL.orePreference[j])) {
               i = j;
               preferred = item;
               if (j == 0) {
                  break label36;
               }
               break;
            }
         }
      }

      preferred = preferred.copy();
      if (!preferred.isEmpty() && preferred.getMetadata() == 32767) {
         preferred.setItemDamage(0);
      }

      return preferred;
   }

   public static NonNullList<ItemDefinition> getItemsFromOre(String ore) {
      LinkedHashSet<ItemDefinition> set = new LinkedHashSet();
      if (!ore.isEmpty()) {
         for(ItemStack item : OreDictionary.getOres(ore, false)) {
            if (item.getMetadata() == 32767) {
               NonNullList<ItemStack> subItems = NonNullList.create();
               item.getItem().getSubItems(item.getItem().getCreativeTab(), subItems);
               set.addAll((Collection)subItems.stream().map(ItemDefinition::of).collect(Collectors.toList()));
            } else {
               set.add(ItemDefinition.of(item));
            }
         }
      }

      return NonNullList.from(ItemDefinition.EMPTY, set.toArray(new ItemDefinition[0]));
   }

   public static NonNullList<ItemStack> getItemsFromOre(String ore, int count) {
      return NonNullList.from(ItemStack.EMPTY, getItemsFromOre(ore).stream().map(ItemDefinition::getItemStack).peek((i) -> {
         i.setCount(count);
      }).toArray((x$0) -> {
         return new ItemStack[x$0];
      }));
   }
}
