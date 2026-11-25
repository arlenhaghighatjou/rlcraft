package mezz.jei.plugins.jei.info;

import javax.annotation.Nullable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.GuiHelper;
import mezz.jei.util.Translator;

public class IngredientInfoRecipeCategory implements IRecipeCategory<IngredientInfoRecipe> {
   public static final int recipeWidth = 160;
   public static final int recipeHeight = 125;
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable slotBackground;
   private final String localizedName;

   public IngredientInfoRecipeCategory(GuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(160, 125);
      this.icon = guiHelper.getInfoIcon();
      this.slotBackground = guiHelper.getSlotDrawable();
      this.localizedName = Translator.translateToLocal("gui.jei.category.itemInformation");
   }

   public String getUid() {
      return "jei.information";
   }

   public String getTitle() {
      return this.localizedName;
   }

   public String getModName() {
      return "Just Enough Items";
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public void setRecipe(IRecipeLayout recipeLayout, IngredientInfoRecipe recipeWrapper, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      int xPos = 71;
      guiItemStacks.init(0, true, xPos, 0);
      guiItemStacks.setBackground(0, this.slotBackground);
      guiItemStacks.set(ingredients);
      IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
      guiFluidStackGroup.init(0, true, xPos + 1, 1);
      guiFluidStackGroup.set(ingredients);
   }
}
