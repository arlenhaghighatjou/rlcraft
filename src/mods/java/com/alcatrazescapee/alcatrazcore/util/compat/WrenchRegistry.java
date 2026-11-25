package com.alcatrazescapee.alcatrazcore.util.compat;

import com.alcatrazescapee.alcatrazcore.AlcatrazCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class WrenchRegistry {
   private static List<ItemStack> wrenches;

   public WrenchRegistry() {
   }

   public static boolean isWrench(ItemStack stack) {
      for(ItemStack wrench : wrenches) {
         if (CoreHelpers.doStacksMatch(stack, wrench)) {
            return true;
         }
      }

      return false;
   }

   public static void init() {
      wrenches = new ArrayList();
      if (Loader.isModLoaded("primalalchemy")) {
         AlcatrazCore.getLog().info("Adding wrenches from Primal Alchemy");
         wrenches.add(CoreHelpers.getStackByRegistryName("primalalchemy:alchemy_wrench", 32767));
      }

      wrenches.removeIf(ItemStack::isEmpty);
   }

   public static void addWrench(ItemStack stack) {
      wrenches.add(stack);
   }
}
