package reborncore.client.gui;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.opengl.GL11;

public class GuiButtonCustomTexture extends GuiButtonExt {
   public int textureU;
   public int textureV;
   public String texturename;
   public String LINKED_PAGE;
   public String NAME;
   public String imageprefix = "techreborn:textures/manual/elements/";
   public int buttonHeight;
   public int buttonWidth;
   public int buttonU;
   public int buttonV;
   public int textureH;
   public int textureW;

   public GuiButtonCustomTexture(int id, int xPos, int yPos, int u, int v, int buttonWidth, int buttonHeight, String texturename, String linkedPage, String name, int buttonU, int buttonV, int textureH, int textureW) {
      super(id, xPos, yPos, buttonWidth, buttonHeight, "_");
      this.textureU = u;
      this.textureV = v;
      this.texturename = texturename;
      this.NAME = name;
      this.LINKED_PAGE = linkedPage;
      this.buttonHeight = this.height;
      this.buttonWidth = this.width;
      this.buttonU = buttonU;
      this.buttonV = buttonV;
      this.textureH = textureH;
      this.textureW = textureW;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
         int u = this.textureU;
         int v = this.textureV;
         if (flag) {
            u = u + this.width;
            GL11.glPushMatrix();
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            this.drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
            GL11.glPopMatrix();
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(32826);
         RenderHelper.enableStandardItemLighting();
         RenderHelper.enableGUIStandardItemLighting();
         this.renderImage(this.x, this.y);
         this.drawString(mc.fontRenderer, this.NAME, this.x + 20, this.y + 3, Color.white.getRGB());
      }

   }

   public void renderImage(int offsetX, int offsetY) {
      TextureManager render = Minecraft.getMinecraft().renderEngine;
      render.bindTexture(new ResourceLocation(this.imageprefix + this.texturename + ".png"));
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(offsetX, offsetY, this.buttonU, this.buttonV, this.textureW, this.textureH);
      GL11.glDisable(3042);
   }

   public boolean getIsHovering() {
      return this.hovered;
   }
}
