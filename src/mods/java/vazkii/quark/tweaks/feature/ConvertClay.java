package vazkii.quark.tweaks.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;

public class ConvertClay extends Feature {
   public ConvertClay() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.CLAY_BALL, 4), new Object[]{ProxyRegistry.newStack(Blocks.CLAY)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
