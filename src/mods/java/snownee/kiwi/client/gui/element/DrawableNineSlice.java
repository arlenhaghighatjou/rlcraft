package snownee.kiwi.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class DrawableNineSlice implements IDrawable {
   private final IDrawableStatic leftTop;
   private final IDrawableStatic leftMiddle;
   private final IDrawableStatic leftBottom;
   private final IDrawableStatic middleTop;
   private final IDrawableStatic middleMiddle;
   private final IDrawableStatic middleBottom;
   private final IDrawableStatic rightTop;
   private final IDrawableStatic rightMiddle;
   private final IDrawableStatic rightBottom;
   private int width;
   private int height;

   public DrawableNineSlice(ResourceLocation resourceLocation, int u, int v, int width, int height, int leftWidth, int rightWidth, int topHeight, int bottomHeight) {
      int uMiddle = u + leftWidth;
      int uRight = u + width - rightWidth;
      int vMiddle = v + topHeight;
      int vBottom = v + height - bottomHeight;
      int middleWidth = uRight - uMiddle;
      int middleHeight = vBottom - vMiddle;
      this.leftTop = new DrawableResource(resourceLocation, u, v, leftWidth, topHeight);
      this.leftMiddle = new DrawableResource(resourceLocation, u, vMiddle, leftWidth, middleHeight);
      this.leftBottom = new DrawableResource(resourceLocation, u, vBottom, leftWidth, bottomHeight);
      this.middleTop = new DrawableResource(resourceLocation, uMiddle, v, middleWidth, topHeight);
      this.middleMiddle = new DrawableResource(resourceLocation, uMiddle, vMiddle, middleWidth, middleHeight);
      this.middleBottom = new DrawableResource(resourceLocation, uMiddle, vBottom, middleWidth, bottomHeight);
      this.rightTop = new DrawableResource(resourceLocation, uRight, v, rightWidth, topHeight);
      this.rightMiddle = new DrawableResource(resourceLocation, uRight, vMiddle, rightWidth, middleHeight);
      this.rightBottom = new DrawableResource(resourceLocation, uRight, vBottom, rightWidth, bottomHeight);
      this.width = width;
      this.height = height;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
      this.leftTop.draw(minecraft, xOffset, yOffset);
      this.leftBottom.draw(minecraft, xOffset, yOffset + this.height - this.leftBottom.getHeight());
      this.rightTop.draw(minecraft, xOffset + this.width - this.rightTop.getWidth(), yOffset);
      this.rightBottom.draw(minecraft, xOffset + this.width - this.rightBottom.getWidth(), yOffset + this.height - this.rightBottom.getHeight());
      int leftWidth = this.leftTop.getWidth();
      int rightWidth = this.rightTop.getWidth();
      int middleWidth = this.width - leftWidth - rightWidth;
      int topHeight = this.leftTop.getHeight();
      int bottomHeight = this.leftBottom.getHeight();
      int middleHeight = this.height - topHeight - bottomHeight;
      if (middleWidth > 0) {
         this.drawTiled(minecraft, xOffset + leftWidth, yOffset, middleWidth, topHeight, this.middleTop);
         this.drawTiled(minecraft, xOffset + leftWidth, yOffset + this.height - this.leftBottom.getHeight(), middleWidth, bottomHeight, this.middleBottom);
      }

      if (middleHeight > 0) {
         this.drawTiled(minecraft, xOffset, yOffset + topHeight, leftWidth, middleHeight, this.leftMiddle);
         this.drawTiled(minecraft, xOffset + this.width - this.rightTop.getWidth(), yOffset + topHeight, rightWidth, middleHeight, this.rightMiddle);
      }

      if (middleHeight > 0 && middleWidth > 0) {
         this.drawTiled(minecraft, xOffset + leftWidth, yOffset + topHeight, middleWidth, middleHeight, this.middleMiddle);
      }

   }

   private void drawTiled(Minecraft minecraft, int xOffset, int yOffset, int tiledWidth, int tiledHeight, IDrawableStatic drawable) {
      int xTileCount = tiledWidth / drawable.getWidth();
      int xRemainder = tiledWidth - xTileCount * drawable.getWidth();
      int yTileCount = tiledHeight / drawable.getHeight();
      int yRemainder = tiledHeight - yTileCount * drawable.getHeight();
      int yStart = yOffset + tiledHeight;

      for(int xTile = 0; xTile <= xTileCount; ++xTile) {
         for(int yTile = 0; yTile <= yTileCount; ++yTile) {
            int width = xTile == xTileCount ? xRemainder : drawable.getWidth();
            int height = yTile == yTileCount ? yRemainder : drawable.getHeight();
            int x = xOffset + xTile * drawable.getWidth();
            int y = yStart - (yTile + 1) * drawable.getHeight();
            if (width > 0 && height > 0) {
               int maskTop = drawable.getHeight() - height;
               int maskRight = drawable.getWidth() - width;
               drawable.draw(minecraft, x, y, maskTop, 0, 0, maskRight);
            }
         }
      }

   }
}
