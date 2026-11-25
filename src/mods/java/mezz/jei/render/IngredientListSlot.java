package mezz.jei.render;

import java.awt.Rectangle;
import javax.annotation.Nullable;

public class IngredientListSlot {
   private final Rectangle area;
   private final int padding;
   private boolean blocked = false;
   @Nullable
   private IngredientRenderer ingredientRenderer;

   public IngredientListSlot(int xPosition, int yPosition, int padding) {
      this.padding = padding;
      int size = 16 + 2 * padding;
      this.area = new Rectangle(xPosition, yPosition, size, size);
   }

   @Nullable
   public IngredientRenderer getIngredientRenderer() {
      return this.ingredientRenderer;
   }

   public void clear() {
      this.ingredientRenderer = null;
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.ingredientRenderer != null && this.area.contains(mouseX, mouseY);
   }

   public void setIngredientRenderer(IngredientRenderer ingredientRenderer) {
      this.ingredientRenderer = ingredientRenderer;
      ingredientRenderer.setArea(this.area);
      ingredientRenderer.setPadding(this.padding);
   }

   public Rectangle getArea() {
      return this.area;
   }

   public void setBlocked(boolean blocked) {
      this.blocked = blocked;
   }

   public boolean isBlocked() {
      return this.blocked;
   }
}
