package betterquesting.api2.client.gui.panels;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;

public class CanvasTextured extends CanvasEmpty {
   private IGuiTexture bgTexture;

   public CanvasTextured(IGuiRect rect, IGuiTexture texture) {
      super(rect);
      this.bgTexture = texture;
   }

   public void changeBG(@Nullable IGuiTexture texture) {
      this.bgTexture = texture;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (this.bgTexture != null) {
         IGuiRect bounds = this.getTransform();
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bgTexture.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
         GlStateManager.popMatrix();
      }

      super.drawPanel(mx, my, partialTick);
   }
}
