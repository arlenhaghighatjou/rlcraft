package slimeknights.mantle.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementScalable extends GuiElement {
   public GuiElementScalable(int x, int y, int w, int h, int texW, int texH) {
      super(x, y, w, h, texW, texH);
   }

   public GuiElementScalable(int x, int y, int w, int h) {
      super(x, y, w, h);
   }

   public int drawScaledX(int xPos, int yPos, int width) {
      for(int i = 0; i < width / this.w; ++i) {
         this.draw(xPos + i * this.w, yPos);
      }

      int remainder = width % this.w;
      if (remainder > 0) {
         GuiScreen.drawModalRectWithCustomSizedTexture(xPos + width - remainder, yPos, (float)this.x, (float)this.y, remainder, this.h, (float)this.texW, (float)this.texH);
      }

      return width;
   }

   public int drawScaledY(int xPos, int yPos, int height) {
      for(int i = 0; i < height / this.h; ++i) {
         this.draw(xPos, yPos + i * this.h);
      }

      int remainder = height % this.h;
      if (remainder > 0) {
         GuiScreen.drawModalRectWithCustomSizedTexture(xPos, yPos + height - remainder, (float)this.x, (float)this.y, this.w, remainder, (float)this.texW, (float)this.texH);
      }

      return this.w;
   }

   public int drawScaled(int xPos, int yPos, int width, int height) {
      int full = height / this.h;

      for(int i = 0; i < full; ++i) {
         this.drawScaledX(xPos, yPos + i * this.h, width);
      }

      yPos = yPos + full * this.h;
      int yRest = height % this.h;

      for(int i = 0; i < width / this.w; ++i) {
         this.drawScaledY(xPos + i * this.w, yPos, yRest);
      }

      int remainder = width % this.w;
      if (remainder > 0) {
         GuiScreen.drawModalRectWithCustomSizedTexture(xPos + width - remainder, yPos, (float)this.x, (float)this.y, remainder, yRest, (float)this.texW, (float)this.texH);
      }

      return width;
   }

   public GuiElementScalable shift(int xd, int yd) {
      GuiElementScalable element = new GuiElementScalable(this.x + xd, this.y + yd, this.w, this.h);
      element.setTextureSize(this.texW, this.texH);
      return element;
   }
}
