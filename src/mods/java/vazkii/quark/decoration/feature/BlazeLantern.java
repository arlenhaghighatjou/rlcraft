package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockBlazeLantern;

public class BlazeLantern extends Feature {
   public static BlockMod blaze_lantern;

   public BlazeLantern() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      blaze_lantern = new BlockBlazeLantern();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(blaze_lantern), new Object[]{"BPB", "PPP", "BPB", 'B', ProxyRegistry.newStack(Items.BLAZE_ROD), 'P', ProxyRegistry.newStack(Items.BLAZE_POWDER)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
