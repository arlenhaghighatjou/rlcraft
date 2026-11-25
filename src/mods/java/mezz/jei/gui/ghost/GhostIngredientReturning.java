package mezz.jei.gui.ghost;

import java.awt.Point;
import java.awt.Rectangle;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;

public class GhostIngredientReturning<T> {
   private static final long DURATION_PER_SCREEN_WIDTH = 500L;
   private final IIngredientRenderer<T> ingredientRenderer;
   private final T ingredient;
   private final Point start;
   private final Point end;
   private final long startTime;
   private final long duration;

   @Nullable
   public static <T> GhostIngredientReturning<T> create(GhostIngredientDrag<T> ghostIngredientDrag, int mouseX, int mouseY) {
      Rectangle origin = ghostIngredientDrag.getOrigin();
      if (origin != null) {
         IIngredientRenderer<T> ingredientRenderer = ghostIngredientDrag.getIngredientRenderer();
         T ingredient = ghostIngredientDrag.getIngredient();
         Point end = new Point(origin.x, origin.y);
         Point start = new Point(mouseX - 8, mouseY - 8);
         return new GhostIngredientReturning<T>(ingredientRenderer, ingredient, start, end);
      } else {
         return null;
      }
   }

   private GhostIngredientReturning(IIngredientRenderer<T> ingredientRenderer, T ingredient, Point start, Point end) {
      this.ingredientRenderer = ingredientRenderer;
      this.ingredient = ingredient;
      this.start = start;
      this.end = end;
      this.startTime = System.currentTimeMillis();
      GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
      if (currentScreen != null) {
         int width = currentScreen.width;
         float durationPerPixel = 500.0F / (float)width;
         float distance = (float)start.distance(end);
         this.duration = (long)Math.round(durationPerPixel * distance);
      } else {
         this.duration = (long)Math.round(250.0F);
      }

   }

   public void drawItem(Minecraft minecraft) {
      long time = System.currentTimeMillis();
      long elapsed = time - this.startTime;
      float percent = Math.min((float)elapsed / (float)this.duration, 1.0F);
      int dx = this.end.x - this.start.x;
      int dy = this.end.y - this.start.y;
      int x = this.start.x + Math.round((float)dx * percent);
      int y = this.start.y + Math.round((float)dy * percent);
      RenderItem renderItem = minecraft.getRenderItem();
      renderItem.zLevel += 150.0F;
      this.ingredientRenderer.render(minecraft, x, y, this.ingredient);
      renderItem.zLevel -= 150.0F;
   }

   public boolean isComplete() {
      long time = System.currentTimeMillis();
      return this.startTime + this.duration < time;
   }
}
