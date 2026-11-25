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
import vazkii.quark.building.block.BlockThatch;
import vazkii.quark.building.block.slab.BlockThatchSlab;
import vazkii.quark.building.block.stairs.BlockThatchStairs;

public class Thatch extends Feature {
   public static Block thatch;
   public static boolean enableStairsAndSlabs;
   public static float fallDamageMultiplier;

   public Thatch() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      fallDamageMultiplier = (float)this.loadPropDouble("Fall damage multiplier", "", 0.5D);
   }

   public void preInit(FMLPreInitializationEvent event) {
      thatch = new BlockThatch();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(thatch, 0, new BlockThatchStairs());
         BlockModSlab.initSlab(thatch, 0, new BlockThatchSlab(false), new BlockThatchSlab(true));
      }

      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(thatch), new Object[]{"WW", "WW", 'W', ProxyRegistry.newStack(Items.WHEAT)});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.WHEAT, 4), new Object[]{ProxyRegistry.newStack(thatch)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
