package mezz.jei.gui.elements;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class DrawableIngredient<V> implements IDrawable {
   private final V ingredient;
   private final IIngredientRenderer<V> ingredientRenderer;

   public DrawableIngredient(V ingredient, IIngredientRenderer<V> ingredientRenderer) {
      this.ingredient = ingredient;
      this.ingredientRenderer = ingredientRenderer;
   }

   public int getWidth() {
      return 16;
   }

   public int getHeight() {
      return 16;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
      GlStateManager.enableDepth();
      this.ingredientRenderer.render(minecraft, xOffset, yOffset, this.ingredient);
      GlStateManager.enableAlpha();
      GlStateManager.disableDepth();
   }
}
