package com.alcatrazescapee.notreepunching.integration.jei;

import com.alcatrazescapee.notreepunching.common.recipe.KnifeRecipe;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@ParametersAreNonnullByDefault
public class KnifeRecipeCategory implements IRecipeCategory<KnifeRecipeCategory.Wrapper> {
   private static final String TRANSLATION_KEY = "jei.category.knife_recipe";
   private static final ResourceLocation GUI_LOCATION = new ResourceLocation("notreepunching", "textures/jei/knife.png");
   private final IDrawable background;
   private final IDrawable icon;

   public KnifeRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(GUI_LOCATION, 0, 0, 135, 18);
      this.icon = guiHelper.createDrawable(GUI_LOCATION, 135, 0, 16, 16);
   }

   @Nonnull
   public String getUid() {
      return "notreepunching.knife";
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public String getTitle() {
      return I18n.format("jei.category.knife_recipe", new Object[0]);
   }

   @Nonnull
   public String getModName() {
      return "No Tree Punching";
   }

   @Nonnull
   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayout recipeLayout, KnifeRecipeCategory.Wrapper wrapper, IIngredients ingredients) {
      int index = 0;
      recipeLayout.getItemStacks().init(index, true, 0, 0);
      recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(ItemStack.class).get(0));
      ++index;
      recipeLayout.getItemStacks().init(index, true, 59, 0);
      recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(ItemStack.class).get(1));
      ++index;
      recipeLayout.getItemStacks().init(index, false, 117, 0);
      recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(ItemStack.class).get(0));
   }

   public static class Wrapper implements IRecipeWrapper {
      private final List<List<ItemStack>> input;
      private final List<List<ItemStack>> output;

      public Wrapper(KnifeRecipe recipe) {
         Builder<List<ItemStack>> builder = ImmutableList.builder();
         builder.add(recipe.getInput().getStacks());
         List<ItemStack> knives = new ArrayList(OreDictionary.getOres("toolKnife"));
         knives.addAll(OreDictionary.getOres("toolWeakKnife"));
         builder.add(knives);
         this.input = builder.build();
         builder = ImmutableList.builder();
         builder.add(Arrays.asList(recipe.getOutput()));
         this.output = builder.build();
      }

      public void getIngredients(@Nonnull IIngredients ingredients) {
         ingredients.setInputLists(ItemStack.class, this.input);
         ingredients.setOutputLists(ItemStack.class, this.output);
      }
   }
}
