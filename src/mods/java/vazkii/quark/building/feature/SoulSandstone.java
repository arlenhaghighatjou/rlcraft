package vazkii.quark.building.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockSoulSandstone;
import vazkii.quark.building.block.slab.BlockSoulSandstoneSlab;
import vazkii.quark.building.block.stairs.BlockSoulSandstoneStairs;

public class SoulSandstone extends Feature {
   public static BlockMod soul_sandstone;
   public static boolean enableStairs;
   public static boolean enableWalls;

   public SoulSandstone() {
   }

   public void setupConfig() {
      enableStairs = this.loadPropBool("Enable stairs", "", true);
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      soul_sandstone = new BlockSoulSandstone();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(soul_sandstone), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(Blocks.SOUL_SAND)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(soul_sandstone, 4, 2), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(soul_sandstone, 1, 0)});
      IBlockState defaultState = soul_sandstone.getDefaultState();
      String slabName = "_slab";
      BlockQuarkSlab halfSlab = new BlockSoulSandstoneSlab(false);
      BlockModSlab.initSlab(soul_sandstone, 32767, halfSlab, new BlockSoulSandstoneSlab(true));
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(soul_sandstone, 1, 1), new Object[]{"S", "S", 'S', ProxyRegistry.newStack(halfSlab, 1, 0)});
      if (enableStairs) {
         BlockModStairs.initStairs(soul_sandstone, 0, new BlockSoulSandstoneStairs());
      }

      VanillaWalls.add("soul_sandstone", soul_sandstone, 0, enableWalls);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
