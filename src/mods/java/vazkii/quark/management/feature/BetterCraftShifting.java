package vazkii.quark.management.feature;

import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterCraftShifting extends Feature {
   public static boolean enableCraftingTable;
   public static boolean enableVillager;

   public BetterCraftShifting() {
   }

   public void setupConfig() {
      enableCraftingTable = this.loadPropBool("Enable Crafting Table", "", true);
      enableVillager = this.loadPropBool("Enable Villager", "", true);
   }

   public static int getMaxInventoryBoundaryCrafting(int min, int max) {
      return ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) && enableCraftingTable && min == 37 && max == 46 ? 10 : max;
   }

   public static int getMaxInventoryBoundaryVillager(int min, int max) {
      return ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) && enableVillager && min == 30 && max == 39 ? 1 : max;
   }

   public static int getMinInventoryBoundaryCrafting(int min, int max) {
      return ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) && enableCraftingTable && min == 37 && max == 46 ? 1 : min;
   }

   public static int getMinInventoryBoundaryVillager(int min, int max) {
      return ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) && enableVillager && min == 30 && max == 39 ? 0 : min;
   }
}
