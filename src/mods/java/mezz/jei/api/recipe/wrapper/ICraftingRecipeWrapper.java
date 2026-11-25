package mezz.jei.api.recipe.wrapper;

import javax.annotation.Nullable;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;

public interface ICraftingRecipeWrapper extends IRecipeWrapper {
   @Nullable
   default ResourceLocation getRegistryName() {
      return null;
   }
}
