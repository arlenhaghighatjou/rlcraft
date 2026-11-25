package mezz.jei.gui.elements;

import mezz.jei.gui.textures.TextureInfo;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class DrawableNineSliceTexture {
   private final TextureInfo info;

   public DrawableNineSliceTexture(TextureInfo info) {
      this.info = info;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset, int width, int height) {
      ResourceLocation location = this.info.getLocation();
      EaglerTextureAtlasSprite sprite = this.info.getSprite();
      int leftWidth = this.info.getSliceLeft();
      int rightWidth = this.info.getSliceRight();
      int topHeight = this.info.getSliceTop();
      int bottomHeight = this.info.getSliceBottom();
      int textureWidth = this.info.getWidth();
      int textureHeight = this.info.getHeight();
      TextureManager textureManager = minecraft.getTextureManager();
      textureManager.bindTexture(location);
      float uMin = sprite.getMinU();
      float uMax = sprite.getMaxU();
      float vMin = sprite.getMinV();
      float vMax = sprite.getMaxV();
      float uSize = uMax - uMin;
      float vSize = vMax - vMin;
      float uLeft = uMin + uSize * ((float)leftWidth / (float)textureWidth);
      float uRight = uMax - uSize * ((float)rightWidth / (float)textureWidth);
      float vTop = vMin + vSize * ((float)topHeight / (float)textureHeight);
      float vBottom = vMax - vSize * ((float)bottomHeight / (float)textureHeight);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferBuilder = tessellator.getBuffer();
      bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      draw(bufferBuilder, uMin, (double)vMin, uLeft, vTop, xOffset, yOffset, leftWidth, topHeight);
      draw(bufferBuilder, uMin, (double)vBottom, uLeft, vMax, xOffset, yOffset + height - bottomHeight, leftWidth, bottomHeight);
      draw(bufferBuilder, uRight, (double)vMin, uMax, vTop, xOffset + width - rightWidth, yOffset, rightWidth, topHeight);
      draw(bufferBuilder, uRight, (double)vBottom, uMax, vMax, xOffset + width - rightWidth, yOffset + height - bottomHeight, rightWidth, bottomHeight);
      int middleWidth = textureWidth - leftWidth - rightWidth;
      int middleHeight = textureWidth - topHeight - bottomHeight;
      int tiledMiddleWidth = width - leftWidth - rightWidth;
      int tiledMiddleHeight = height - topHeight - bottomHeight;
      if (tiledMiddleWidth > 0) {
         this.drawTiled(bufferBuilder, uLeft, vMin, uRight, vTop, xOffset + leftWidth, yOffset, tiledMiddleWidth, topHeight, middleWidth, topHeight);
         this.drawTiled(bufferBuilder, uLeft, vBottom, uRight, vMax, xOffset + leftWidth, yOffset + height - bottomHeight, tiledMiddleWidth, bottomHeight, middleWidth, bottomHeight);
      }

      if (tiledMiddleHeight > 0) {
         this.drawTiled(bufferBuilder, uMin, vTop, uLeft, vBottom, xOffset, yOffset + topHeight, leftWidth, tiledMiddleHeight, leftWidth, middleHeight);
         this.drawTiled(bufferBuilder, uRight, vTop, uMax, vBottom, xOffset + width - rightWidth, yOffset + topHeight, rightWidth, tiledMiddleHeight, rightWidth, middleHeight);
      }

      if (tiledMiddleHeight > 0 && tiledMiddleWidth > 0) {
         this.drawTiled(bufferBuilder, uLeft, vTop, uRight, vBottom, xOffset + leftWidth, yOffset + topHeight, tiledMiddleWidth, tiledMiddleHeight, middleWidth, middleHeight);
      }

      tessellator.draw();
   }

   private void drawTiled(WorldRenderer bufferBuilder, float uMin, float vMin, float uMax, float vMax, int xOffset, int yOffset, int tiledWidth, int tiledHeight, int width, int height) {
      int xTileCount = tiledWidth / width;
      int xRemainder = tiledWidth - xTileCount * width;
      int yTileCount = tiledHeight / height;
      int yRemainder = tiledHeight - yTileCount * height;
      int yStart = yOffset + tiledHeight;
      float uSize = uMax - uMin;
      float vSize = vMax - vMin;

      for(int xTile = 0; xTile <= xTileCount; ++xTile) {
         for(int yTile = 0; yTile <= yTileCount; ++yTile) {
            int tileWidth = xTile == xTileCount ? xRemainder : width;
            int tileHeight = yTile == yTileCount ? yRemainder : height;
            int x = xOffset + xTile * width;
            int y = yStart - (yTile + 1) * height;
            if (tileWidth > 0 && tileHeight > 0) {
               int maskRight = width - tileWidth;
               int maskTop = height - tileHeight;
               float uOffset = (float)maskRight / (float)width * uSize;
               float vOffset = (float)maskTop / (float)height * vSize;
               draw(bufferBuilder, uMin, (double)(vMin + vOffset), uMax - uOffset, vMax, x, y + maskTop, tileWidth, tileHeight);
            }
         }
      }

   }

   private static void draw(WorldRenderer bufferBuilder, float minU, double minV, float maxU, float maxV, int xOffset, int yOffset, int width, int height) {
      bufferBuilder.pos((double)xOffset, (double)(yOffset + height), 0.0D).tex((double)minU, (double)maxV).endVertex();
      bufferBuilder.pos((double)(xOffset + width), (double)(yOffset + height), 0.0D).tex((double)maxU, (double)maxV).endVertex();
      bufferBuilder.pos((double)(xOffset + width), (double)yOffset, 0.0D).tex((double)maxU, minV).endVertex();
      bufferBuilder.pos((double)xOffset, (double)yOffset, 0.0D).tex((double)minU, minV).endVertex();
   }
}
