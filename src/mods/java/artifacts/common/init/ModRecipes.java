package artifacts.common.init;

import artifacts.common.recipe.ArtifactPhantomThreadRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.IForgeRegistry;

public class ModRecipes {
   public ModRecipes() {
   }

   public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
      if (Loader.isModLoaded("classyhats")) {
         registry.register((new ArtifactPhantomThreadRecipe()).setRegistryName(new ResourceLocation("artifacts", "artifacts_phantom_thread")));
      }

   }
}
