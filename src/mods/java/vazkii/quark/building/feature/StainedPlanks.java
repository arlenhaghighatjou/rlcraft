package vazkii.quark.building.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockStainedPlanks;
import vazkii.quark.building.block.slab.BlockStainedPlanksSlab;
import vazkii.quark.building.block.stairs.BlockStainedPlanksStairs;

public class StainedPlanks extends Feature {
   public static BlockMod stained_planks;
   public static boolean enableStairsAndSlabs;

   public StainedPlanks() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      stained_planks = new BlockStainedPlanks();
      if (enableStairsAndSlabs) {
         for(BlockStainedPlanks.Variants variant : BlockStainedPlanks.Variants.values()) {
            BlockModStairs.initStairs(stained_planks, variant.ordinal(), new BlockStainedPlanksStairs(variant));
         }

         for(BlockStainedPlanks.Variants variant : BlockStainedPlanks.Variants.values()) {
            BlockModSlab.initSlab(stained_planks, variant.ordinal(), new BlockStainedPlanksSlab(variant, false), new BlockStainedPlanksSlab(variant, true));
         }
      }

      for(int i = 0; i < 16; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(stained_planks, 8, i), new Object[]{"BBB", "BDB", "BBB", 'B', "plankWood", 'D', LibMisc.OREDICT_DYES.get(15 - i)});
      }

      addOreDict("plankWood", ProxyRegistry.newStack(stained_planks, 1, 32767));
      addOreDict("plankStained", ProxyRegistry.newStack(stained_planks, 1, 32767));
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
