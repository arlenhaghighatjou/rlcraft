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
import vazkii.quark.building.block.BlockSnowBricks;
import vazkii.quark.building.block.slab.BlockSnowBricksSlab;
import vazkii.quark.building.block.stairs.BlockSnowBricksStairs;
import vazkii.quark.building.block.wall.BlockSnowBricksWall;

public class SnowBricks extends Feature {
   public static Block snow_bricks;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public SnowBricks() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      snow_bricks = new BlockSnowBricks();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(snow_bricks, 0, new BlockSnowBricksStairs());
         BlockModSlab.initSlab(snow_bricks, 0, new BlockSnowBricksSlab(false), new BlockSnowBricksSlab(true));
      }

      VanillaWalls.add("snow_bricks", snow_bricks, 0, enableWalls, BlockSnowBricksWall::new);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(snow_bricks, 4), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(Blocks.SNOW)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
