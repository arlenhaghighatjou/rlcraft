package mezz.jei.plugins.vanilla.furnace;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.config.Constants;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class FurnaceSmeltingCategory extends FurnaceRecipeCategory<SmeltingRecipe> {
   private final IDrawable background;
   private final IDrawable icon;
   private final String localizedName;

   public FurnaceSmeltingCategory(IGuiHelper guiHelper) {
      super(guiHelper);
      this.background = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 0, 114, 82, 54);
      this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.FURNACE));
      this.localizedName = Translator.translateToLocal("gui.jei.category.smelting");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void drawExtras(Minecraft minecraft) {
      this.animatedFlame.draw(minecraft, 1, 20);
      this.arrow.draw(minecraft, 24, 18);
   }

   public String getTitle() {
      return this.localizedName;
   }

   public String getModName() {
      return "Minecraft";
   }

   public String getUid() {
      return "minecraft.smelting";
   }

   public void setRecipe(IRecipeLayout recipeLayout, SmeltingRecipe recipeWrapper, IIngredients ingredients) {
      IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
      guiItemStacks.init(0, true, 0, 0);
      guiItemStacks.init(2, false, 60, 18);
      guiItemStacks.set(ingredients);
   }
}
