package mezz.jei.plugins.vanilla.furnace;

import javax.annotation.Nullable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.config.Constants;
import mezz.jei.gui.GuiHelper;
import mezz.jei.util.Translator;

public class FurnaceFuelCategory extends FurnaceRecipeCategory<FuelRecipe> {
   private final IDrawableStatic background;
   private final IDrawableStatic flameTransparentBackground;
   private final String localizedName;

   public FurnaceFuelCategory(GuiHelper guiHelper) {
      super(guiHelper);
      this.background = guiHelper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 0, 134, 18, 34).addPadding(0, 0, 0, 88).build();
      this.flameTransparentBackground = guiHelper.getFlameIcon();
      this.localizedName = Translator.translateToLocal("gui.jei.category.fuel");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public String getUid() {
      return "minecraft.fuel";
   }

   public String getTitle() {
      return this.localizedName;
   }

   public String getModName() {
      return "Minecraft";
   }

   @Nullable
   public IDrawable getIcon() {
      return this.flameTransparentBackground;
   }

   public void setRecipe(IRecipeLayout recipeLayout, FuelRecipe recipeWrapper, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      guiItemStacks.init(1, true, 0, 16);
      guiItemStacks.set(ingredients);
   }
}
