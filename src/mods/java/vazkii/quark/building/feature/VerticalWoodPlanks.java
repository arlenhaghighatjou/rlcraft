package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.block.BlockVerticalPlanks;
import vazkii.quark.building.block.BlockVerticalStainedPlanks;

public class VerticalWoodPlanks extends Feature {
   public static Block vertical_planks;
   public static Block vertical_stained_planks;
   public static boolean enableVerticalStained = true;

   public VerticalWoodPlanks() {
   }

   public void setupConfig() {
      enableVerticalStained = this.loadPropBool("Enable Vertical Stained Planks", "", enableVerticalStained);
   }

   public void preInit(FMLPreInitializationEvent event) {
      vertical_planks = new BlockVerticalPlanks();

      for(int i = 0; i < 6; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(vertical_planks, 3, i), new Object[]{"W", "W", "W", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i)});
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.PLANKS, 3, i), new Object[]{"W", "W", "W", 'W', ProxyRegistry.newStack(vertical_planks, 1, i)});
      }

      if (ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained) {
         vertical_stained_planks = new BlockVerticalStainedPlanks();
      }

      this.addOreDict();
   }

   public void postPreInit() {
      if (ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained) {
         for(int i = 0; i < 16; ++i) {
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(vertical_stained_planks, 3, i), new Object[]{"W", "W", "W", 'W', ProxyRegistry.newStack(StainedPlanks.stained_planks, 1, i)});
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(StainedPlanks.stained_planks, 3, i), new Object[]{"W", "W", "W", 'W', ProxyRegistry.newStack(vertical_stained_planks, 1, i)});
         }
      }

   }

   private void addOreDict() {
      addOreDict("plankWood", ProxyRegistry.newStack(vertical_planks, 1, 32767));
      if (ModuleLoader.isFeatureEnabled(StainedPlanks.class) && enableVerticalStained) {
         addOreDict("plankWood", ProxyRegistry.newStack(vertical_stained_planks, 1, 32767));
         addOreDict("plankStained", ProxyRegistry.newStack(vertical_stained_planks, 1, 32767));
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
