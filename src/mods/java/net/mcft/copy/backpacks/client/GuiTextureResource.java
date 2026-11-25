package net.mcft.copy.backpacks.client;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTextureResource extends ResourceLocation {
   public final int defaultWidth;
   public final int defaultHeight;

   public GuiTextureResource(String location, int defaultWidth, int defaultHeight) {
      super("wearablebackpacks", "textures/gui/" + location + ".png");
      this.defaultWidth = defaultWidth;
      this.defaultHeight = defaultHeight;
   }

   public void bind() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(this);
   }

   public void drawQuad(int x, int y, int u, int v, int w, int h, float zLevel) {
      float scaleX = 1.0F / (float)this.defaultWidth;
      float scaleY = 1.0F / (float)this.defaultHeight;
      Tessellator tess = Tessellator.getInstance();
      WorldRenderer vb = tess.getBuffer();
      vb.begin(7, DefaultVertexFormats.POSITION_TEX);
      vb.pos((double)(x + 0), (double)(y + h), (double)zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + h) * scaleY)).endVertex();
      vb.pos((double)(x + w), (double)(y + h), (double)zLevel).tex((double)((float)(u + w) * scaleX), (double)((float)(v + h) * scaleY)).endVertex();
      vb.pos((double)(x + w), (double)(y + 0), (double)zLevel).tex((double)((float)(u + w) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
      vb.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + 0) * scaleY)).endVertex();
      tess.draw();
   }

   public void drawQuad(int x, int y, int u, int v, int w, int h) {
      this.drawQuad(x, y, u, v, w, h, 0.0F);
   }
}
