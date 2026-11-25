package slimeknights.mantle.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElement {
   public static int defaultTexW = 256;
   public static int defaultTexH = 256;
   public final int x;
   public final int y;
   public final int w;
   public final int h;
   public int texW;
   public int texH;

   public GuiElement(int x, int y, int w, int h, int texW, int texH) {
      this(x, y, w, h);
      this.setTextureSize(texW, texH);
      defaultTexW = texW;
      defaultTexH = texH;
   }

   public GuiElement(int x, int y, int w, int h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.setTextureSize(defaultTexW, defaultTexH);
   }

   public GuiElement setTextureSize(int w, int h) {
      this.texW = w;
      this.texH = h;
      return this;
   }

   public GuiElement shift(int xd, int yd) {
      return new GuiElement(this.x + xd, this.y + yd, this.w, this.h, this.texW, this.texH);
   }

   public int draw(int xPos, int yPos) {
      GuiScreen.drawModalRectWithCustomSizedTexture(xPos, yPos, (float)this.x, (float)this.y, this.w, this.h, (float)this.texW, (float)this.texH);
      return this.w;
   }

   public static class Builder {
      public int w;
      public int h;

      public Builder(int w, int h) {
         this.w = w;
         this.h = h;
      }

      public GuiElement get(int x, int y, int w, int h) {
         return new GuiElement(x, y, w, h, this.w, this.h);
      }
   }
}
