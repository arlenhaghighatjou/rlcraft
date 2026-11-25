package rustic.compat.jei;

import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class AdvancedAlchemyRecipeCategory extends BlankRecipeCategory {
   private final IDrawable background;
   private final String name;
   public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_alchemy_advanced.png");
   protected final IDrawableAnimated flame;
   protected final IDrawableAnimated arrow;

   public AdvancedAlchemyRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(texture, 0, 0, 130, 79);
      this.name = I18n.format("rustic.jei.recipe.advanced_alchemy", new Object[0]);
      IDrawableStatic flameDrawable = helper.createDrawable(texture, 130, 0, 14, 14);
      this.flame = helper.createAnimatedDrawable(flameDrawable, 300, StartDirection.TOP, true);
      IDrawableStatic arrowDrawable = helper.createDrawable(texture, 130, 14, 50, 53);
      this.arrow = helper.createAnimatedDrawable(arrowDrawable, 300, StartDirection.LEFT, false);
   }

   public void drawExtras(Minecraft minecraft) {
      this.flame.draw(minecraft, 44, 43);
      this.arrow.draw(minecraft, 21, 14);
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public String getTitle() {
      return this.name;
   }

   public String getUid() {
      return "rustic.alchemy_advanced";
   }

   public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
      IGuiItemStackGroup stacks = layout.getItemStacks();
      IGuiFluidStackGroup fluid = layout.getFluidStacks();
      stacks.init(2, true, 81, 3);
      if (ingredients.getInputs(VanillaTypes.ITEM).size() > 0) {
         stacks.set(2, (List)ingredients.getInputs(VanillaTypes.ITEM).get(0));
      }

      stacks.init(3, true, 42, 3);
      if (ingredients.getInputs(VanillaTypes.ITEM).size() > 1) {
         stacks.set(3, (List)ingredients.getInputs(VanillaTypes.ITEM).get(1));
      }

      stacks.init(4, true, 3, 55);
      stacks.init(5, true, 3, 31);
      stacks.init(6, true, 3, 7);
      if (ingredients.getInputs(VanillaTypes.ITEM).size() > 2) {
         stacks.set(4, (List)ingredients.getInputs(VanillaTypes.ITEM).get(2));
      }

      if (ingredients.getInputs(VanillaTypes.ITEM).size() > 3) {
         stacks.set(5, (List)ingredients.getInputs(VanillaTypes.ITEM).get(3));
      }

      if (ingredients.getInputs(VanillaTypes.ITEM).size() > 4) {
         stacks.set(6, (List)ingredients.getInputs(VanillaTypes.ITEM).get(4));
      }

      fluid.init(0, true, 110, 24, 16, 32, 8000, true, (IDrawable)null);
      if (ingredients.getInputs(VanillaTypes.FLUID).size() > 0) {
         fluid.set(0, (List)ingredients.getInputs(VanillaTypes.FLUID).get(0));
      }

      stacks.init(0, false, 81, 31);
      if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0) {
         stacks.set(0, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(0));
      }

   }

   public String getModName() {
      return "Rustic";
   }
}
