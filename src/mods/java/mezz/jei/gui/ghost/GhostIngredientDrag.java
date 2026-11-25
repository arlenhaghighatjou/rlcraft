package mezz.jei.gui.ghost;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import org.lwjgl.opengl.GL11;

public class GhostIngredientDrag<T> {
   private static final Color targetColor = new Color(19, 201, 10, 64);
   private static final Color hoverColor = new Color(76, 201, 25, 128);
   private final IGhostIngredientHandler<?> handler;
   private final List<IGhostIngredientHandler.Target<T>> targets;
   private final IIngredientRenderer<T> ingredientRenderer;
   private final T ingredient;
   @Nullable
   private final Rectangle origin;

   public GhostIngredientDrag(IGhostIngredientHandler<?> handler, List<IGhostIngredientHandler.Target<T>> targets, IIngredientRenderer<T> ingredientRenderer, T ingredient, @Nullable Rectangle origin) {
      this.handler = handler;
      this.targets = targets;
      this.ingredientRenderer = ingredientRenderer;
      this.ingredient = ingredient;
      this.origin = origin;
   }

   public void drawTargets(int mouseX, int mouseY) {
      if (this.handler.shouldHighlightTargets()) {
         List<IGhostIngredientHandler.Target<Object>> targets = (List)this.targets;
         drawTargets(mouseX, mouseY, targets);
      }

   }

   public void drawItem(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.origin != null) {
         int originX = this.origin.x + this.origin.width / 2;
         int originY = this.origin.y + this.origin.height / 2;
         int xDist = originX - mouseX;
         int yDist = originY - mouseY;
         float lineWidth = 2.0F;
         if (minecraft.currentScreen != null) {
            long distanceSq = (long)(xDist * xDist + yDist * yDist);
            int screenDim = minecraft.currentScreen.width * minecraft.currentScreen.height;
            float percentOfDim = Math.min(1.0F, (float)distanceSq / (float)screenDim);
            lineWidth = 1.0F + (1.0F - percentOfDim) * 3.0F;
         }

         GL11.glDisable(3553);
         GlStateManager.disableDepth();
         GL11.glLineWidth(lineWidth);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glBegin(1);
         GL11.glColor4f((float)targetColor.getRed() / 255.0F, (float)targetColor.getGreen() / 255.0F, (float)targetColor.getBlue() / 255.0F, (float)targetColor.getAlpha() / 255.0F);
         GL11.glVertex3f((float)mouseX, (float)mouseY, 150.0F);
         GL11.glVertex3f((float)originX, (float)originY, 150.0F);
         GL11.glEnd();
         GlStateManager.enableDepth();
         GL11.glEnable(3553);
      }

      RenderItem renderItem = minecraft.getRenderItem();
      renderItem.zLevel += 150.0F;
      this.ingredientRenderer.render(minecraft, mouseX - 8, mouseY - 8, this.ingredient);
      renderItem.zLevel -= 150.0F;
   }

   public static void drawTargets(int mouseX, int mouseY, List<IGhostIngredientHandler.Target<Object>> targets) {
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();

      for(IGhostIngredientHandler.Target target : targets) {
         Rectangle area = target.getArea();
         Color color;
         if (area.contains(mouseX, mouseY)) {
            color = hoverColor;
         } else {
            color = targetColor;
         }

         Gui.drawRect(area.x, area.y, area.x + area.width, area.y + area.height, color.getRGB());
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean onClick(int mouseX, int mouseY) {
      for(IGhostIngredientHandler.Target<T> target : this.targets) {
         Rectangle area = target.getArea();
         if (area.contains(mouseX, mouseY)) {
            target.accept(this.ingredient);
            this.handler.onComplete();
            return true;
         }
      }

      this.handler.onComplete();
      return false;
   }

   public void stop() {
      this.handler.onComplete();
   }

   public IIngredientRenderer<T> getIngredientRenderer() {
      return this.ingredientRenderer;
   }

   public T getIngredient() {
      return this.ingredient;
   }

   @Nullable
   public Rectangle getOrigin() {
      return this.origin;
   }
}
