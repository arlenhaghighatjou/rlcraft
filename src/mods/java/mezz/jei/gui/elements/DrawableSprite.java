package mezz.jei.gui.elements;

import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.gui.textures.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class DrawableSprite implements IDrawableStatic {
   private final TextureInfo info;

   public DrawableSprite(TextureInfo info) {
      this.info = info;
   }

   public int getWidth() {
      return this.info.getWidth();
   }

   public int getHeight() {
      return this.info.getHeight();
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
      this.draw(minecraft, xOffset, yOffset, 0, 0, 0, 0);
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
      ResourceLocation location = this.info.getLocation();
      TextureAtlasSprite sprite = this.info.getSprite();
      int textureWidth = this.info.getWidth();
      int textureHeight = this.info.getHeight();
      TextureManager textureManager = minecraft.getTextureManager();
      textureManager.bindTexture(location);
      maskTop = maskTop + this.info.getTrimTop();
      maskBottom = maskBottom + this.info.getTrimBottom();
      maskLeft = maskLeft + this.info.getTrimLeft();
      maskRight = maskRight + this.info.getTrimRight();
      int x = xOffset + maskLeft;
      int y = yOffset + maskTop;
      int width = textureWidth - maskRight - maskLeft;
      int height = textureHeight - maskBottom - maskTop;
      float uSize = sprite.getMaxU() - sprite.getMinU();
      float vSize = sprite.getMaxV() - sprite.getMinV();
      float minU = sprite.getMinU() + uSize * ((float)maskLeft / (float)textureWidth);
      float minV = sprite.getMinV() + vSize * ((float)maskTop / (float)textureHeight);
      float maxU = sprite.getMaxU() - uSize * ((float)maskRight / (float)textureWidth);
      float maxV = sprite.getMaxV() - vSize * ((float)maskBottom / (float)textureHeight);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferBuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)minU, (double)maxV).endVertex();
      bufferBuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)maxU, (double)maxV).endVertex();
      bufferBuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)maxU, (double)minV).endVertex();
      bufferBuilder.pos((double)x, (double)y, 0.0D).tex((double)minU, (double)minV).endVertex();
      tessellator.draw();
   }
}
