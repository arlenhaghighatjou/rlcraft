package net.mcft.copy.backpacks.client.gui;

import net.mcft.copy.backpacks.client.GuiTextureResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiImage extends GuiElementBase {
   private GuiTextureResource _texture;
   private int _texU;
   private int _texV;

   public GuiImage(int width, int height, GuiTextureResource texture) {
      this(width, height, texture, 0, 0);
   }

   public GuiImage(int width, int height, GuiTextureResource texture, int u, int v) {
      this(0, 0, width, height, texture, u, v);
   }

   public GuiImage(int x, int y, int width, int height, GuiTextureResource texture, int u, int v) {
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setTexture(texture, u, v);
      this.setTextureUV(u, v);
   }

   public GuiTextureResource getTexture() {
      return this._texture;
   }

   public void setTexture(GuiTextureResource value) {
      this._texture = value;
   }

   public int getTextureU() {
      return this._texU;
   }

   public int getTextureV() {
      return this._texV;
   }

   public void setTextureUV(int u, int v) {
      this._texU = u;
      this._texV = v;
   }

   public void setTexture(GuiTextureResource texture, int u, int v) {
      this.setTexture(texture);
      this.setTextureUV(u, v);
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      GuiTextureResource texture = this.getTexture();
      if (texture != null) {
         setRenderColorARGB(GuiElementBase.Color.WHITE);
         texture.bind();
         texture.drawQuad(0, 0, this.getTextureU(), this.getTextureV(), this.getWidth(), this.getHeight());
      }
   }
}
