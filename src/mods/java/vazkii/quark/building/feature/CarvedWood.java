package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockCarvedWood;

public class CarvedWood extends Feature {
   public static BlockMod carvedWood;

   public CarvedWood() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      carvedWood = new BlockCarvedWood();

      for(int i = 0; i < 6; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(carvedWood, 2, i), new Object[]{"WW", "WW", 'W', ProxyRegistry.newStack(Blocks.WOODEN_SLAB, 1, i)});
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
