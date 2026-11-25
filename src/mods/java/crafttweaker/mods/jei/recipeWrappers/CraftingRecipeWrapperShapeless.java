package crafttweaker.mods.jei.recipeWrappers;

import crafttweaker.mc1120.recipes.MCRecipeBase;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import crafttweaker.mc1120.recipes.MCRecipeShapeless;
import crafttweaker.mods.jei.JEIAddonPlugin;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class CraftingRecipeWrapperShapeless implements ICraftingRecipeWrapper {
   private final MCRecipeShapeless recipe;

   public CraftingRecipeWrapperShapeless(MCRecipeShapeless recipe) {
      this.recipe = recipe;
   }

   public static void registerCraftingRecipes() {
      Stream var10000 = MCRecipeManager.recipesToAdd.stream().map(MCRecipeManager.ActionBaseAddRecipe::getRecipe);
      MCRecipeShapeless.class.getClass();
      var10000 = var10000.filter(MCRecipeShapeless.class::isInstance).filter(MCRecipeBase::isVisible);
      MCRecipeShapeless.class.getClass();
      List<CraftingRecipeWrapperShapeless> wrappers = (List)var10000.map(MCRecipeShapeless.class::cast).map(MCRecipeShapeless::update).map(CraftingRecipeWrapperShapeless::new).collect(Collectors.toList());
      JEIAddonPlugin.modRegistry.addRecipes(wrappers, "minecraft.crafting");
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
      ingredients.setInputLists(VanillaTypes.ITEM, (List)this.recipe.getIngredients().stream().map(Ingredient::getMatchingStacks).map(Arrays::asList).collect(Collectors.toList()));
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.recipe.getRegistryName();
   }
}
