package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class BrewingRecipeCategory extends BlankRecipeCategory {
   private final IDrawable background;
   private final String name;
   public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_brewing.png");
   protected final IDrawableAnimated bubbles;
   protected final IDrawableAnimated arrow;
   protected final IDrawableAnimated arrow1;

   public BrewingRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(texture, 0, 0, 130, 80);
      this.name = I18n.format("rustic.jei.recipe.brewing", new Object[0]);
      IDrawableStatic bubbleDrawable = helper.createDrawable(texture, 130, 0, 11, 28);
      this.bubbles = helper.createAnimatedDrawable(bubbleDrawable, 100, StartDirection.BOTTOM, false);
      IDrawableStatic arrowDrawable = helper.createDrawable(texture, 130, 28, 24, 16);
      this.arrow = helper.createAnimatedDrawable(arrowDrawable, 1200, StartDirection.LEFT, false);
      IDrawableStatic arrow1Drawable = helper.createDrawable(texture, 130, 44, 10, 10);
      this.arrow1 = helper.createAnimatedDrawable(arrow1Drawable, 1200, StartDirection.LEFT, false);
   }

   public void drawExtras(Minecraft minecraft) {
      this.bubbles.draw(minecraft, 117, 26);
      this.arrow.draw(minecraft, 63, 32);
      this.arrow1.draw(minecraft, 23, 35);
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public String getTitle() {
      return this.name;
   }

   public String getUid() {
      return "rustic.brewing";
   }

   public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
      IGuiFluidStackGroup fluid = layout.getFluidStacks();
      fluid.init(0, true, 40, 24, 16, 32, 8000, true, (IDrawable)null);
      fluid.set(0, (List)ingredients.getInputs(FluidStack.class).get(0));
      fluid.init(1, false, 94, 24, 16, 32, 8000, true, (IDrawable)null);
      fluid.set(1, (List)ingredients.getOutputs(FluidStack.class).get(0));
      fluid.init(2, true, 4, 32, 16, 16, 1000, true, (IDrawable)null);
      FluidStack aux = ((FluidStack)((List)ingredients.getOutputs(FluidStack.class).get(0)).get(0)).copy();
      FluidStack auxDummy = ((FluidStack)((List)ingredients.getOutputs(FluidStack.class).get(0)).get(0)).copy();
      aux.amount = 1000;
      auxDummy.amount = 0;
      List<FluidStack> auxList = new ArrayList();
      auxList.add(auxDummy);
      auxList.add(aux);
      fluid.set(2, auxList);
      fluid.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
         if (slotIndex == 2 && ingredient.amount == 0) {
            tooltip.clear();
         }

      });
   }

   public String getModName() {
      return "Rustic";
   }
}
