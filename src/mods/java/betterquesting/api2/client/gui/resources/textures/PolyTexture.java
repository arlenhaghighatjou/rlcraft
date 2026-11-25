package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class PolyTexture implements IGuiTexture {
   private final IGuiColor defColor;
   private final boolean shadow;
   private final double[] verts;
   private int borderSize = 1;
   private IGuiColor borColor = new GuiColorStatic(-1);

   public PolyTexture(int points, double rotation, boolean shadow, @Nonnull IGuiColor color) {
      this.defColor = color;
      this.shadow = shadow;
      if (points <= 0) {
         points = 32;
      }

      this.verts = new double[points * 2];
      double min = 1.0E-4D;

      for(int i = 0; i < points; ++i) {
         double angle = 360.0D * ((double)i / (double)points);
         double x = -Math.sin(Math.toRadians(angle + rotation));
         double y = -Math.cos(Math.toRadians(angle + rotation));
         min = Math.max(min, Math.max(Math.abs(x), Math.abs(y)));
         this.verts[i * 2] = x;
         this.verts[i * 2 + 1] = y;
      }

      for(int i = 0; i < this.verts.length; ++i) {
         double x = this.verts[i];
         x = x / 2.0D / min + 0.5D;
         this.verts[i] = x;
      }

   }

   public PolyTexture(@Nonnull double[] verts, boolean shadow, IGuiColor color) {
      this.defColor = color;
      this.shadow = shadow;
      this.verts = new double[verts.length];
      System.arraycopy(verts, 0, this.verts, 0, verts.length);
   }

   public IGuiTexture setBorder(int size, IGuiColor color) {
      this.borderSize = size;
      this.borColor = color;
      return this;
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick) {
      this.drawTexture(x, y, width, height, zDepth, partialTick, this.defColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         GlStateManager.pushMatrix();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder vertexbuffer = tessellator.getBuffer();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         int w = this.shadow ? width - 2 : width;
         int h = this.shadow ? height - 2 : height;
         int dx = this.shadow ? x + 1 : x;
         int dy = this.shadow ? y + 1 : y;
         int sx = x + 2;
         int sy = y + 2;
         int points = this.verts.length / 2;
         if (this.shadow) {
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F);
            vertexbuffer.begin(9, DefaultVertexFormats.POSITION);

            for(int i = 0; i < points + 1; ++i) {
               int index = i % points * 2;
               vertexbuffer.pos((double)sx + (double)w * this.verts[index], (double)sy + (double)h * this.verts[index + 1], 0.0D).endVertex();
            }

            tessellator.draw();
         }

         color.applyGlColor();
         vertexbuffer.begin(9, DefaultVertexFormats.POSITION);

         for(int i = 0; i < points + 1; ++i) {
            int index = i % points * 2;
            vertexbuffer.pos((double)dx + (double)w * this.verts[index], (double)dy + (double)h * this.verts[index + 1], 0.0D).endVertex();
         }

         tessellator.draw();
         if (this.borderSize > 0 && this.borColor != null) {
            this.borColor.applyGlColor();
            vertexbuffer.begin(8, DefaultVertexFormats.POSITION);

            for(int i = 0; i < points + 1; ++i) {
               int index = i % points * 2;
               double bx = (this.verts[index] - 0.5D) * (double)(-this.borderSize) * 2.0D;
               double by = (this.verts[index + 1] - 0.5D) * (double)(-this.borderSize) * 2.0D;
               vertexbuffer.pos((double)dx + (double)w * this.verts[index] + bx, (double)dy + (double)h * this.verts[index + 1] + by, 0.0D).endVertex();
               vertexbuffer.pos((double)dx + (double)w * this.verts[index], (double)dy + (double)h * this.verts[index + 1], 0.0D).endVertex();
            }

            tessellator.draw();
         }

         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return null;
   }

   public IGuiRect getBounds() {
      return null;
   }
}
