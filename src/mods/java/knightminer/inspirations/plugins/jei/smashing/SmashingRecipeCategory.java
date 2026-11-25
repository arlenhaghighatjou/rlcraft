package knightminer.inspirations.plugins.jei.smashing;

import javax.annotation.Nonnull;
import knightminer.inspirations.library.Util;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class SmashingRecipeCategory implements IRecipeCategory<SmashingRecipeWrapper> {
   public static final String CATEGORY = Util.prefix("anvil_smashing");
   public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/anvil_smashing.png");
   private final IDrawable background;

   public SmashingRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 33, 0, 4, 0, 0);
   }

   @Nonnull
   public String getUid() {
      return CATEGORY;
   }

   @Nonnull
   public String getTitle() {
      return Util.translate("gui.jei.anvil_smashing.title");
   }

   @Nonnull
   public IDrawable getBackground() {
      return this.background;
   }

   public void setRecipe(IRecipeLayout recipeLayout, SmashingRecipeWrapper recipeWrapper, IIngredients ingredients) {
      IGuiItemStackGroup items = recipeLayout.getItemStacks();
      items.init(0, true, 43, 15);
      items.set(ingredients);
      items.init(1, false, 97, 15);
      items.set(ingredients);
   }

   public String getModName() {
      return "Inspirations";
   }
}
