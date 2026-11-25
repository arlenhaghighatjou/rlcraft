package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class SlicedTexture implements IGuiTexture {
   private static final IGuiColor defColor = new GuiColorStatic(255, 255, 255, 255);
   private final ResourceLocation texture;
   private final IGuiRect texBounds;
   private final GuiPadding texBorder;
   private SlicedTexture.SliceMode sliceMode = SlicedTexture.SliceMode.SLICED_TILE;

   public SlicedTexture(ResourceLocation tex, IGuiRect bounds, GuiPadding border) {
      this.texture = tex;
      this.texBounds = bounds;
      this.texBorder = border;
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick) {
      this.drawTexture(x, y, width, height, zLevel, partialTick, defColor);
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0) {
         int w = Math.max(width, this.texBorder.getLeft() + this.texBorder.getRight());
         int h = Math.max(height, this.texBorder.getTop() + this.texBorder.getBottom());
         int dx = x;
         int dy = y;
         GlStateManager.pushMatrix();
         color.applyGlColor();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         if (w != width || h != height) {
            dx = 0;
            dy = 0;
            GlStateManager.translate((float)x, (float)y, 0.0F);
            GlStateManager.scale((double)width / (double)w, (double)height / (double)h, 1.0D);
         }

         if (this.sliceMode == SlicedTexture.SliceMode.SLICED_TILE) {
            drawContinuousTexturedBox(this.texture, dx, dy, this.texBounds.getX(), this.texBounds.getY(), w, h, this.texBounds.getWidth(), this.texBounds.getHeight(), this.texBorder.getTop(), this.texBorder.getBottom(), this.texBorder.getLeft(), this.texBorder.getRight(), zLevel);
         } else if (this.sliceMode == SlicedTexture.SliceMode.SLICED_STRETCH) {
            int iu = this.texBounds.getX() + this.texBorder.getLeft();
            int iv = this.texBounds.getY() + this.texBorder.getTop();
            int iw = this.texBounds.getWidth() - this.texBorder.getLeft() - this.texBorder.getRight();
            int ih = this.texBounds.getHeight() - this.texBorder.getTop() - this.texBorder.getBottom();
            float sx = (float)(w - (this.texBounds.getWidth() - iw)) / (float)iw;
            float sy = (float)(h - (this.texBounds.getHeight() - ih)) / (float)ih;
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)dx, (float)dy, 0.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX(), this.texBounds.getY(), this.texBorder.getLeft(), this.texBorder.getTop(), zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + this.texBorder.getLeft()), (float)dy, 0.0F);
            GlStateManager.scale(sx, 1.0F, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX() + this.texBorder.getLeft(), this.texBounds.getY(), iw, this.texBorder.getTop(), zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + w - this.texBorder.getRight()), (float)dy, 0.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX() + this.texBorder.getLeft() + iw, this.texBounds.getY(), this.texBorder.getRight(), this.texBorder.getTop(), zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)dx, (float)(dy + this.texBorder.getTop()), 0.0F);
            GlStateManager.scale(1.0F, sy, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX(), this.texBounds.getY() + this.texBorder.getTop(), this.texBorder.getLeft(), ih, zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + this.texBorder.getLeft()), (float)(dy + this.texBorder.getTop()), 0.0F);
            GlStateManager.scale(sx, sy, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, iu, iv, iw, ih, zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + w - this.texBorder.getRight()), (float)(dy + this.texBorder.getTop()), 0.0F);
            GlStateManager.scale(1.0F, sy, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX() + this.texBorder.getLeft() + iw, this.texBounds.getY() + this.texBorder.getTop(), this.texBorder.getRight(), ih, zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)dx, (float)(dy + h - this.texBorder.getBottom()), 0.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX(), this.texBounds.getY() + this.texBorder.getTop() + ih, this.texBorder.getLeft(), this.texBorder.getBottom(), zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + this.texBorder.getLeft()), (float)(dy + h - this.texBorder.getBottom()), 0.0F);
            GlStateManager.scale(sx, 1.0F, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX() + this.texBorder.getLeft(), this.texBounds.getY() + this.texBorder.getTop() + ih, iw, this.texBorder.getBottom(), zLevel);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(dx + w - this.texBorder.getRight()), (float)(dy + h - this.texBorder.getBottom()), 0.0F);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX() + this.texBorder.getLeft() + iw, this.texBounds.getY() + this.texBorder.getTop() + ih, this.texBorder.getRight(), this.texBorder.getBottom(), zLevel);
            GlStateManager.popMatrix();
         } else {
            float sx = (float)w / (float)this.texBounds.getWidth();
            float sy = (float)h / (float)this.texBounds.getHeight();
            GlStateManager.translate((float)dx, (float)dy, 0.0F);
            GlStateManager.scale(sx, sy, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
            GuiUtils.drawTexturedModalRect(0, 0, this.texBounds.getX(), this.texBounds.getY(), this.texBounds.getWidth(), this.texBounds.getHeight(), zLevel);
         }

         GlStateManager.popMatrix();
      }
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public IGuiRect getBounds() {
      return this.texBounds;
   }

   public GuiPadding getBorder() {
      return this.texBorder;
   }

   public SlicedTexture setSliceMode(SlicedTexture.SliceMode mode) {
      this.sliceMode = mode;
      return this;
   }

   public static SlicedTexture readFromJson(JsonObject json) {
      ResourceLocation res = new ResourceLocation(JsonHelper.GetString(json, "texture", "minecraft:missingno"));
      int slice = JsonHelper.GetNumber(json, "sliceMode", Integer.valueOf(1)).intValue();
      JsonObject jOut = JsonHelper.GetObject(json, "coordinates");
      int ox = JsonHelper.GetNumber(jOut, "u", Integer.valueOf(0)).intValue();
      int oy = JsonHelper.GetNumber(jOut, "v", Integer.valueOf(0)).intValue();
      int ow = JsonHelper.GetNumber(jOut, "w", Integer.valueOf(48)).intValue();
      int oh = JsonHelper.GetNumber(jOut, "h", Integer.valueOf(48)).intValue();
      JsonObject jIn = JsonHelper.GetObject(json, "border");
      int il = JsonHelper.GetNumber(jIn, "l", Integer.valueOf(16)).intValue();
      int it = JsonHelper.GetNumber(jIn, "t", Integer.valueOf(16)).intValue();
      int ir = JsonHelper.GetNumber(jIn, "r", Integer.valueOf(16)).intValue();
      int ib = JsonHelper.GetNumber(jIn, "b", Integer.valueOf(16)).intValue();
      return (new SlicedTexture(res, new GuiRectangle(ox, oy, ow, oh), new GuiPadding(il, it, ir, ib))).setSliceMode(SlicedTexture.SliceMode.values()[slice % 3]);
   }

   private static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
      Minecraft.getMinecraft().renderEngine.bindTexture(res);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      int fillerWidth = textureWidth - leftBorder - rightBorder;
      int fillerHeight = textureHeight - topBorder - bottomBorder;
      if (fillerWidth > 0 && fillerHeight > 0) {
         int canvasWidth = width - leftBorder - rightBorder;
         int canvasHeight = height - topBorder - bottomBorder;
         int xPasses = canvasWidth / fillerWidth;
         int remainderWidth = canvasWidth % fillerWidth;
         int yPasses = canvasHeight / fillerHeight;
         int remainderHeight = canvasHeight % fillerHeight;
         GuiUtils.drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
         GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
         GuiUtils.drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
         GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

         for(int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); ++i) {
            GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, zLevel);
            GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, zLevel);

            for(int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
               GuiUtils.drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, zLevel);
            }
         }

         for(int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
            GuiUtils.drawTexturedModalRect(x, y + topBorder + j * fillerHeight, u, v + topBorder, leftBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
            GuiUtils.drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + j * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
         }

      }
   }

   public static enum SliceMode {
      STRETCH,
      SLICED_TILE,
      SLICED_STRETCH;

      private SliceMode() {
      }
   }
}
