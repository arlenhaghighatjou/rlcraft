package lykrast.defiledlands.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;

public class DefilementCategory implements IRecipeCategory<DefilementWrapper> {
   private final IDrawable background;
   private final String localizedName;
   public static final String UID = "dldefilement";

   public DefilementCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(DefiledLandsJEI.GUI, 0, 0, 76, 18);
      this.localizedName = I18n.format("ui.defiledlands.jei.defilement", new Object[0]);
   }

   public String getUid() {
      return "dldefilement";
   }

   public String getTitle() {
      return this.localizedName;
   }

   public String getModName() {
      return "defiledlands";
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public void setRecipe(IRecipeLayout recipeLayout, DefilementWrapper recipeWrapper, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      guiItemStacks.init(0, true, 0, 0);
      guiItemStacks.init(1, false, 58, 0);
      guiItemStacks.set(ingredients);
   }
}
