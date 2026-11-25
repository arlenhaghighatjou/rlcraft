package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class SimpleTexture implements IGuiTexture {
   private static final IGuiColor defColor = new GuiColorStatic(255, 255, 255, 255);
   private final ResourceLocation texture;
   private final IGuiRect texBounds;
   private boolean maintainAspect = false;

   public SimpleTexture(ResourceLocation texture, IGuiRect bounds) {
      this.texture = texture;
      this.texBounds = bounds;
   }

   public SimpleTexture maintainAspect(boolean enable) {
      this.maintainAspect = enable;
      return this;
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick) {
      this.drawTexture(x, y, width, height, zLevel, partialTick, defColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         GlStateManager.pushMatrix();
         float sx = (float)width / (float)this.texBounds.getWidth();
         float sy = (float)height / (float)this.texBounds.getHeight();
         if (this.maintainAspect) {
            float sa = Math.min(sx, sy);
            float dx = (sx - sa) * (float)this.texBounds.getWidth() / 2.0F;
            float dy = (sy - sa) * (float)this.texBounds.getHeight() / 2.0F;
            sx = sa;
            sy = sa;
            GlStateManager.translate((float)x + dx, (float)y + dy, 0.0F);
         } else {
            GlStateManager.translate((float)x, (float)y, 0.0F);
         }

         GlStateManager.scale(sx, sy, 1.0F);
         color.applyGlColor();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
         GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX(), this.texBounds.getY(), this.texBounds.getWidth(), this.texBounds.getHeight(), zLevel);
         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public IGuiRect getBounds() {
      return this.texBounds;
   }
}
