package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ColorTexture implements IGuiTexture {
   private final IGuiColor baseColor;
   private final GuiRectangle rect;
   private final GuiPadding pad;
   private final IGuiRect bounds;

   public ColorTexture(IGuiColor baseColor) {
      this(baseColor, new GuiPadding(0, 0, 0, 0));
   }

   public ColorTexture(IGuiColor baseColor, GuiPadding padding) {
      this.rect = new GuiRectangle(0, 0, 0, 0);
      this.bounds = new GuiRectangle(0, 0, 16, 16);
      this.baseColor = baseColor;
      this.pad = padding;
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
      this.drawTexture(x, y, width, height, zDepth, partialTick, this.baseColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         GlStateManager.pushMatrix();
         this.rect.x = x + this.pad.l;
         this.rect.y = y + this.pad.t;
         this.rect.w = width - (this.pad.r + this.pad.l);
         this.rect.h = height - (this.pad.b + this.pad.r);
         RenderUtils.drawColoredRect(this.rect, color);
         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return PresetTexture.TX_SIMPLE;
   }

   public IGuiRect getBounds() {
      return this.bounds;
   }
}
