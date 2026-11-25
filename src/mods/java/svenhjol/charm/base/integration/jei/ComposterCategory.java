package svenhjol.charm.base.integration.jei;

import java.util.Iterator;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ComposterCategory implements IRecipeCategory {
   private final IGuiHelper guiHelper;
   public static final String UID = "CHARM_COMPOSTER_CATEGORY";

   public ComposterCategory(IGuiHelper guiHelper) {
      this.guiHelper = guiHelper;
   }

   public String getUid() {
      return "CHARM_COMPOSTER_CATEGORY";
   }

   public String getTitle() {
      return "Composter";
   }

   public String getModName() {
      return "Charm";
   }

   public IDrawable getBackground() {
      return this.guiHelper.createDrawable(new ResourceLocation("charm", "textures/gui/composter_jei.png"), 7, 5, 163, 120);
   }

   public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipe, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      int y = 12;
      int i = 0;
      int j = 0;
      int k = 0;
      Iterator var9 = ingredients.getInputs(ItemStack.class).iterator();

      while(var9.hasNext()) {
         for(ItemStack stack : (List)var9.next()) {
            guiItemStacks.init(i, true, j * 18, y + k * 18);
            guiItemStacks.set(i, stack);
            ++i;
            j = i % 9;
            k += j == 0 ? 1 : 0;
         }
      }

      y = y + 85;
      i = 36;
      j = 0;
      k = 0;
      var9 = ingredients.getOutputs(ItemStack.class).iterator();

      while(var9.hasNext()) {
         for(ItemStack stack : (List)var9.next()) {
            guiItemStacks.init(i, true, j * 18, y + k * 18);
            guiItemStacks.set(i, stack);
            ++i;
            j = i % 9;
            k += j == 0 ? 1 : 0;
         }
      }

   }
}
