package com.alcatrazescapee.notreepunching.integration.jei;

import com.alcatrazescapee.notreepunching.common.recipe.FirePitRecipe;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class FirePitRecipeCategory implements IRecipeCategory<FirePitRecipeCategory.Wrapper> {
   private static final ResourceLocation GUI_LOCATION = new ResourceLocation("notreepunching", "textures/jei/fire_pit.png");
   private static final String TRANSLATION_KEY = "jei.category.firepit_recipe";
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawableAnimated animatedArrow;
   private final IDrawableAnimated animatedFlame;

   public FirePitRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(GUI_LOCATION, 0, 0, 74, 54);
      this.icon = guiHelper.createDrawable(GUI_LOCATION, 111, 0, 16, 16);
      IDrawableStatic staticArrow = guiHelper.createDrawable(GUI_LOCATION, 88, 0, 16, 23);
      this.animatedArrow = guiHelper.createAnimatedDrawable(staticArrow, 200, StartDirection.LEFT, false);
      IDrawableStatic staticFlame = guiHelper.createDrawable(GUI_LOCATION, 74, 0, 14, 13);
      this.animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, StartDirection.TOP, true);
   }

   @Nonnull
   public String getUid() {
      return "notreepunching.fire_pit";
   }

   @Nonnull
   public String getTitle() {
      return I18n.format("jei.category.firepit_recipe", new Object[0]);
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

   @SideOnly(Side.CLIENT)
   public void drawExtras(Minecraft minecraft) {
      this.animatedArrow.draw(minecraft, 26, 1);
      this.animatedFlame.draw(minecraft, 30, 20);
   }

   public void setRecipe(IRecipeLayout recipeLayout, FirePitRecipeCategory.Wrapper wrapper, IIngredients ingredients) {
      int index = 0;
      recipeLayout.getItemStacks().init(index, true, 0, 0);
      recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(ItemStack.class).get(0));
      ++index;
      recipeLayout.getItemStacks().init(index, false, 56, 0);
      recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(ItemStack.class).get(0));
   }

   public static class Wrapper implements IRecipeWrapper {
      private final List<List<ItemStack>> input;
      private final List<List<ItemStack>> output;

      public Wrapper(FirePitRecipe recipe) {
         Builder<List<ItemStack>> builder = ImmutableList.builder();
         builder.add(recipe.getInput().getStacks());
         this.input = builder.build();
         builder = ImmutableList.builder();
         builder.add(ImmutableList.of(recipe.getOutput()));
         this.output = builder.build();
      }

      public void getIngredients(@Nonnull IIngredients ingredients) {
         ingredients.setInputLists(ItemStack.class, this.input);
         ingredients.setOutputLists(ItemStack.class, this.output);
      }
   }
}
