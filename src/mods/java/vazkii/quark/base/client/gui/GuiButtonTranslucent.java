package vazkii.quark.base.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiButtonTranslucent extends GuiButton {
   public GuiButtonTranslucent(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      super(buttonId, x, y, widthIn, heightIn, buttonText);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      drawRect(x, y, x + width, y + height, Integer.MIN_VALUE);
   }

   public void drawActualTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)x, (double)(y + height), (double)this.zLevel).tex((double)((float)textureX * f), (double)((float)(textureY + height) * f)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)y, (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)textureY * f)).endVertex();
      bufferbuilder.pos((double)x, (double)y, (double)this.zLevel).tex((double)((float)textureX * f), (double)((float)textureY * f)).endVertex();
      tessellator.draw();
   }
}
