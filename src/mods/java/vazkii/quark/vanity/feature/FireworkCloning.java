package vazkii.quark.vanity.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.recipe.FireworkCloningRecipe;

public class FireworkCloning extends Feature {
   public FireworkCloning() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      new FireworkCloningRecipe();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
