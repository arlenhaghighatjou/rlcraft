package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import java.util.function.Predicate;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionarySW {
   public static final String QUIVERS = "spartanweaponry:quivers";
   public static final String ARROW_QUIVERS = "spartanweaponry:arrow_quivers";
   public static final String BOLT_QUIVERS = "spartanweaponry:bolt_quivers";
   public static final String ARROWS = "spartanweaponry:arrows";
   public static final String BOLTS = "spartanweaponry:bolts";

   public OreDictionarySW() {
   }

   public static void init() {
      Log.info("Registering OreDictionary entries");
      register("spartanweaponry:quivers", ItemRegistrySW.quiverArrowLight, ItemRegistrySW.quiverArrowModerate, ItemRegistrySW.quiverArrowHeavy, ItemRegistrySW.quiverBoltLight, ItemRegistrySW.quiverBoltModerate, ItemRegistrySW.quiverBoltHeavy);
      register("spartanweaponry:arrow_quivers", ItemRegistrySW.quiverArrowLight, ItemRegistrySW.quiverArrowModerate, ItemRegistrySW.quiverArrowHeavy);
      register("spartanweaponry:bolt_quivers", ItemRegistrySW.quiverBoltLight, ItemRegistrySW.quiverBoltModerate, ItemRegistrySW.quiverBoltHeavy);
      if (!ConfigHandler.disableQuivers) {
         OreDictionary.registerOre("spartanweaponry:arrows", Items.ARROW);
         if (!ConfigHandler.disableNewArrows) {
            register("spartanweaponry:arrows", ItemRegistrySW.arrowWood, ItemRegistrySW.arrowIron);
            if (!ConfigHandler.disableDiamondArrowsAndBolts) {
               OreDictionary.registerOre("spartanweaponry:arrows", ItemRegistrySW.arrowDiamond);
            }
         }

         if (!ConfigHandler.disableCrossbow) {
            register("spartanweaponry:bolts", ItemRegistrySW.bolt, ItemRegistrySW.boltSpectral);
            if (!ConfigHandler.disableDiamondArrowsAndBolts) {
               OreDictionary.registerOre("spartanweaponry:bolts", ItemRegistrySW.boltDiamond);
            }
         }

      }
   }

   public static void register(String name, Item... items) {
      for(Item item : items) {
         if (item != null) {
            OreDictionary.registerOre(name, item);
         }
      }

   }

   public static boolean matches(String name, ItemStack stack) {
      NonNullList<ItemStack> list = OreDictionary.getOres(name);
      return list.isEmpty() ? false : list.stream().anyMatch((listStack) -> {
         return stack.isItemEqualIgnoreDurability(listStack);
      });
   }
}
