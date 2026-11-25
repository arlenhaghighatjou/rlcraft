package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidTexture implements IGuiTexture {
   private static final IGuiColor defColor = new GuiColorStatic(255, 255, 255, 255);
   private final FluidStack fluid;
   private final boolean showCount;
   private final boolean keepAspect;
   private final IGuiRect bounds;

   public FluidTexture(FluidStack fluid) {
      this(fluid, false, true);
   }

   public FluidTexture(FluidStack fluid, boolean showCount, boolean keepAspect) {
      this.bounds = new GuiRectangle(0, 0, 16, 16);
      this.fluid = fluid;
      this.showCount = showCount;
      this.keepAspect = keepAspect;
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
      this.drawTexture(x, y, width, height, zDepth, partialTick, defColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         float sx = (float)width / 16.0F;
         float sy = (float)height / 16.0F;
         double dx = 0.0D;
         double dy = 0.0D;
         if (this.keepAspect) {
            float sa = Math.min(sx, sy);
            dx = Math.floor((double)((sx - sa) * 8.0F));
            dy = Math.floor((double)((sy - sa) * 8.0F));
            sx = sa;
            sy = sa;
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate((double)x + dx, (double)y + dy, 0.0D);
         GlStateManager.scale(sx, sy, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         int fCol = this.fluid.getFluid().getColor(this.fluid);
         float a = (float)(fCol >> 24 & 255) / 255.0F;
         float r = (float)(fCol >> 16 & 255) / 255.0F;
         float g = (float)(fCol >> 8 & 255) / 255.0F;
         float b = (float)(fCol & 255) / 255.0F;
         a = a + color.getAlpha() / 2.0F;
         r = r + color.getRed() / 2.0F;
         g = g + color.getGreen() / 2.0F;
         b = b + color.getBlue() / 2.0F;
         GlStateManager.color(r, g, b, a);
         Minecraft mc = Minecraft.getMinecraft();
         mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         TextureAtlasSprite fluidTx = mc.getTextureMapBlocks().getAtlasSprite(this.fluid.getFluid().getStill().toString());
         this.drawTexturedModalRect(0.0D, 0.0D, 0.0D, fluidTx, 16.0D, 16.0D);
         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return PresetTexture.TX_NULL;
   }

   public IGuiRect getBounds() {
      return this.bounds;
   }

   private void drawTexturedModalRect(double xCoord, double yCoord, double zDepth, TextureAtlasSprite textureSprite, double widthIn, double heightIn) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(xCoord, yCoord + heightIn, zDepth).tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV()).endVertex();
      bufferbuilder.pos(xCoord + widthIn, yCoord + heightIn, zDepth).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV()).endVertex();
      bufferbuilder.pos(xCoord + widthIn, yCoord, zDepth).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).endVertex();
      bufferbuilder.pos(xCoord, yCoord, zDepth).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).endVertex();
      tessellator.draw();
   }
}
