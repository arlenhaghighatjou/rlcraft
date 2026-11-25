package pl.asie.foamfix.coremod.injections.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import pl.asie.foamfix.client.ModelResourceLocationFastConstructHelper;

public class ModelResourceLocationFastConstructInject extends ModelResourceLocation {
   public ModelResourceLocationFastConstructInject(ResourceLocation location, String variantIn) {
      super(0, ModelResourceLocationFastConstructHelper.rlVariantToArray(location, variantIn));
   }
}
