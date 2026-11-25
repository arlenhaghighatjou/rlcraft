package goblinbob.mobends.core.client.gui;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class CustomFontRenderer {
   protected CustomFont font;
   protected int characterSpacing = 1;

   public CustomFontRenderer() {
   }

   public void setFont(CustomFont font) {
      this.font = font;
   }

   protected void drawSymbol(CustomFont.Symbol symbol, WorldRenderer vertexBuffer, int x, int y) {
      x = x + symbol.offsetX;
      y = y + symbol.offsetY;
      int width = symbol.width;
      int height = symbol.height;
      float textureX = (float)symbol.u / (float)this.font.atlasWidth;
      float textureY = (float)symbol.v / (float)this.font.atlasHeight;
      float textureWidth = (float)width / (float)this.font.atlasWidth;
      float textureHeight = (float)height / (float)this.font.atlasHeight;
      vertexBuffer.pos((double)x, (double)y, 0.0D).tex((double)textureX, (double)(textureY + textureHeight)).endVertex();
      vertexBuffer.pos((double)(x + width), (double)y, 0.0D).tex((double)(textureX + textureWidth), (double)(textureY + textureHeight)).endVertex();
      vertexBuffer.pos((double)(x + width), (double)(y - height), 0.0D).tex((double)(textureX + textureWidth), (double)textureY).endVertex();
      vertexBuffer.pos((double)x, (double)(y - height), 0.0D).tex((double)textureX, (double)textureY).endVertex();
   }

   public int getTextWidth(String textToDraw) {
      int width = 0;

      for(int i = 0; i < textToDraw.length(); ++i) {
         CustomFont.Symbol symbol = this.font.getSymbol(textToDraw.charAt(i));
         width += symbol.width;
         if (i != textToDraw.length() - 1) {
            width += this.characterSpacing;
         }
      }

      return width;
   }

   public void drawText(String textToDraw, int x, int y) {
      if (this.font != null) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.font.resourceLocation);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer vertexbuffer = tessellator.getBuffer();
         vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
         int nextCharX = x;

         for(int i = 0; i < textToDraw.length(); ++i) {
            CustomFont.Symbol symbol = this.font.getSymbol(textToDraw.charAt(i));
            this.drawSymbol(symbol, vertexbuffer, nextCharX, y);
            nextCharX += symbol.width + this.characterSpacing;
         }

         tessellator.draw();
      }
   }

   public void drawCenteredText(String textToDraw, int x, int y) {
      int width = this.getTextWidth(textToDraw);
      this.drawText(textToDraw, x - width / 2, y);
   }
}
