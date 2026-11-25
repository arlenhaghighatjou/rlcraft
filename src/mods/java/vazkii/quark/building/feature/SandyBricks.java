package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockSandyBricks;
import vazkii.quark.building.block.slab.BlockSandyBricksSlab;
import vazkii.quark.building.block.stairs.BlockSandyBricksStairs;

public class SandyBricks extends Feature {
   public static Block sandy_bricks;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public SandyBricks() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      sandy_bricks = new BlockSandyBricks();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(sandy_bricks, 0, new BlockSandyBricksStairs());
         BlockModSlab.initSlab(sandy_bricks, 0, new BlockSandyBricksSlab(false), new BlockSandyBricksSlab(true));
      }

      VanillaWalls.add("sandy_bricks", sandy_bricks, 0, enableWalls);
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(sandy_bricks), new Object[]{ProxyRegistry.newStack(Blocks.BRICK_BLOCK), ProxyRegistry.newStack(Blocks.SAND)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
