package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockReed;
import vazkii.quark.building.block.slab.BlockReedSlab;
import vazkii.quark.building.block.stairs.BlockReedStairs;

public class ReedBlock extends Feature {
   public static Block reed_block;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public ReedBlock() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      reed_block = new BlockReed();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(reed_block, 0, new BlockReedStairs());
         BlockModSlab.initSlab(reed_block, 0, new BlockReedSlab(false), new BlockReedSlab(true));
      }

      VanillaWalls.add("reed_block", reed_block, 0, enableWalls);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(reed_block), new Object[]{"RRR", "RRR", "RRR", 'R', ProxyRegistry.newStack(Items.REEDS)});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.REEDS, 9), new Object[]{ProxyRegistry.newStack(reed_block)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
