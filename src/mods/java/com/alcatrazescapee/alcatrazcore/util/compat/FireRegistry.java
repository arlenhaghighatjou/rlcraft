package com.alcatrazescapee.alcatrazcore.util.compat;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class FireRegistry {
   private static final List<ItemStack> FIRE_STARTERS = new ArrayList();

   public FireRegistry() {
   }

   public static boolean isFireStarter(ItemStack stack) {
      for(ItemStack fireStarter : FIRE_STARTERS) {
         if (CoreHelpers.doStacksMatch(stack, fireStarter)) {
            return true;
         }
      }

      return false;
   }

   public static void init() {
      FIRE_STARTERS.clear();
      FIRE_STARTERS.add(new ItemStack(Items.FLINT_AND_STEEL, 1, 32767));
      if (Loader.isModLoaded("notreepunching")) {
         FIRE_STARTERS.add(CoreHelpers.getStackByRegistryName("notreepunching:fire_starter", 32767));
      }

      if (Loader.isModLoaded("charcoal_pit")) {
         FIRE_STARTERS.add(CoreHelpers.getStackByRegistryName("charcoal_pit:fire_starter", 32767));
      }

      if (Loader.isModLoaded("primal")) {
         FIRE_STARTERS.add(CoreHelpers.getStackByRegistryName("primal:fire_bow", 32767));
      }

      FIRE_STARTERS.removeIf(ItemStack::isEmpty);
   }

   public static void addFireStarter(ItemStack stack) {
      FIRE_STARTERS.add(stack);
   }
}
