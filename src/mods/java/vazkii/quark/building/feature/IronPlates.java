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
import vazkii.quark.building.block.BlockIronPlate;
import vazkii.quark.building.block.slab.BlockIronPlateSlab;
import vazkii.quark.building.block.stairs.BlockIronPlateStairs;

public class IronPlates extends Feature {
   public static Block iron_plate;
   public static boolean enableStairsAndSlabs;

   public IronPlates() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      iron_plate = new BlockIronPlate();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(iron_plate, 0, new BlockIronPlateStairs());
         BlockModSlab.initSlab(iron_plate, 0, new BlockIronPlateSlab(false), new BlockIronPlateSlab(true));
      }

      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_plate, 24), new Object[]{"III", "I I", "III", 'I', "ingotIron"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_plate, 24, 1), new Object[]{"III", "IBI", "III", 'I', "ingotIron", 'B', ProxyRegistry.newStack(Items.WATER_BUCKET)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_plate, 8, 1), new Object[]{"III", "IBI", "III", 'I', ProxyRegistry.newStack(iron_plate), 'B', ProxyRegistry.newStack(Items.WATER_BUCKET)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
