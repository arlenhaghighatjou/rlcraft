package com.alcatrazescapee.notreepunching.integration.guide.utils;

import amerifrance.guideapi.api.IRecipeRenderer;
import amerifrance.guideapi.page.PageIRecipe;
import amerifrance.guideapi.page.reciperenderer.ShapedOreRecipeRenderer;
import amerifrance.guideapi.page.reciperenderer.ShapedRecipesRenderer;
import amerifrance.guideapi.page.reciperenderer.ShapelessOreRecipeRenderer;
import amerifrance.guideapi.page.reciperenderer.ShapelessRecipesRenderer;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class PageIRecipeLazy extends PageIRecipe implements ILazyLoader {
   private final ResourceLocation recipeLookup;

   private static IRecipeRenderer getRenderer(IRecipe recipe) {
      if (recipe instanceof ShapedRecipes) {
         return new ShapedRecipesRenderer((ShapedRecipes)recipe);
      } else if (recipe instanceof ShapelessRecipes) {
         return new ShapelessRecipesRenderer((ShapelessRecipes)recipe);
      } else if (recipe instanceof ShapedOreRecipe) {
         return new ShapedOreRecipeRenderer((ShapedOreRecipe)recipe);
      } else if (recipe instanceof ShapelessOreRecipe) {
         return new ShapelessOreRecipeRenderer((ShapelessOreRecipe)recipe);
      } else {
         NoTreePunching.getLog().info("Couldn't get renderer!!! Problem!!!");
         return null;
      }
   }

   public PageIRecipeLazy(ResourceLocation recipeLookup) {
      super((IRecipe)null, (IRecipeRenderer)null);
      this.recipeLookup = recipeLookup;
   }

   public void loadPost() {
      this.recipe = (IRecipe)ForgeRegistries.RECIPES.getValue(this.recipeLookup);
      this.iRecipeRenderer = getRenderer(this.recipe);
      this.isValid = this.recipe != null && this.iRecipeRenderer != null;
   }
}
