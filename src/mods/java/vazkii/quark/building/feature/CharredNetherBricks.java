package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockCharredNetherBricks;
import vazkii.quark.building.block.slab.BlockCharredNetherBrickSlab;
import vazkii.quark.building.block.stairs.BlockCharredNetherBrickStairs;

public class CharredNetherBricks extends Feature {
   public static Block charred_nether_bricks;
   public static boolean enableStairsAndSlabs;

   public CharredNetherBricks() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      charred_nether_bricks = new BlockCharredNetherBricks();
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(charred_nether_bricks, 3), new Object[]{ProxyRegistry.newStack(Blocks.NETHER_BRICK), ProxyRegistry.newStack(Blocks.NETHER_BRICK), ProxyRegistry.newStack(Blocks.NETHER_BRICK), ProxyRegistry.newStack(Items.FIRE_CHARGE)});
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(charred_nether_bricks, 0, new BlockCharredNetherBrickStairs());
         BlockModSlab.initSlab(charred_nether_bricks, 0, new BlockCharredNetherBrickSlab(false), new BlockCharredNetherBrickSlab(true));
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
