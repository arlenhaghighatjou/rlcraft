package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockPolishedNetherrack;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;

public class PolishedNetherrack extends Feature {
   public static BlockMod polished_netherrack;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public PolishedNetherrack() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      polished_netherrack = new BlockPolishedNetherrack();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(polished_netherrack, 1, new BlockQuarkStairs("polished_netherrack_bricks_stairs", polished_netherrack.getDefaultState().withProperty(polished_netherrack.getVariantProp(), BlockPolishedNetherrack.Variants.POLISHED_NETHERRACK_BRICKS)));
         BlockBasicStoneSlab.initSlab(polished_netherrack, 1, "polished_netherrack_bricks_slab");
      }

      VanillaWalls.add("polished_netherrack_bricks", polished_netherrack, 1, enableWalls);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(polished_netherrack), new Object[]{"RR", "RR", 'R', ProxyRegistry.newStack(Blocks.NETHERRACK)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(polished_netherrack, 4, 1), new Object[]{"RR", "RR", 'R', ProxyRegistry.newStack(polished_netherrack)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
