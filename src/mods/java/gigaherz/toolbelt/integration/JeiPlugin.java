package gigaherz.toolbelt.integration;

import com.google.common.collect.Lists;
import gigaherz.toolbelt.Config;
import gigaherz.toolbelt.ToolBelt;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JeiPlugin implements IModPlugin {
   public JeiPlugin() {
   }

   public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
   }

   public void registerIngredients(IModIngredientRegistration registry) {
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
   }

   public void register(IModRegistry registry) {
      if (!Config.disableAnvilUpgrading) {
         IJeiHelpers helpers = registry.getJeiHelpers();
         IVanillaRecipeFactory vanillaFactory = helpers.getVanillaRecipeFactory();
         List<ItemStack> pouch = Collections.singletonList(new ItemStack(ToolBelt.pouch));
         registry.addRecipes(Lists.newArrayList(new IRecipeWrapper[]{vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(0), pouch, Collections.singletonList(ToolBelt.belt.of(1))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(1), pouch, Collections.singletonList(ToolBelt.belt.of(2))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(2), pouch, Collections.singletonList(ToolBelt.belt.of(3))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(3), pouch, Collections.singletonList(ToolBelt.belt.of(4))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(4), pouch, Collections.singletonList(ToolBelt.belt.of(5))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(5), pouch, Collections.singletonList(ToolBelt.belt.of(6))), vanillaFactory.createAnvilRecipe(ToolBelt.belt.of(6), pouch, Collections.singletonList(ToolBelt.belt.of(7)))}), "minecraft.anvil");
      }
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
   }
}
