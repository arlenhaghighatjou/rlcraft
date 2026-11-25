package vazkii.arl.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import vazkii.arl.recipe.MultiRecipe;

@JEIPlugin
public class JeiPlugin implements IModPlugin {
   public JeiPlugin() {
   }

   public void register(IModRegistry registry) {
      for(IRecipe recipe : CraftingManager.REGISTRY) {
         if (recipe instanceof MultiRecipe) {
            registry.addRecipes(((MultiRecipe)recipe).getRecipes(), "minecraft.crafting");
         }
      }

   }
}
