package pl.asie.foamfix.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class ModelResourceLocationFastConstructHelper extends ModelResourceLocation {
   protected ModelResourceLocationFastConstructHelper(int unused, String... resourceName) {
      super(unused, resourceName);
   }

   public static String[] rlVariantToArray(ResourceLocation location, String variantIn) {
      return location.getClass() == ResourceLocation.class ? new String[]{location.getResourceDomain(), location.getResourcePath(), variantIn} : ModelResourceLocation.parsePathString(location + "#" + (variantIn == null ? "normal" : variantIn));
   }
}
