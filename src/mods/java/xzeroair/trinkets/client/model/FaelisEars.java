package xzeroair.trinkets.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class FaelisEars extends ModelBase {
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/ears.png");

   public FaelisEars() {
      this.textureWidth = 64;
      this.textureHeight = 64;
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
      float fscale = 0.3F;
      GlStateManager.scale(0.3F, 0.3F, 0.3F);
      double x = 0.0D;
      double y = -2.4D;
      double height = 1.0D;
      double width = 1.0D;
      double z = -0.8D;
      float u = 32.0F;
      float v = 0.0F;
      int uWidth = 16;
      int vHeight = 16;
      float tileWidth = 64.0F;
      float tileHeight = 32.0F;
      double xR = 0.0D;
      double xL = -0.0D;
      this.DrawRightEar(0.0D, -2.4D, -0.8D, 32.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F);
      this.DrawRightEar(0.0D, -2.4D, -0.7999D, 32.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F);
      this.DrawLeftEar(0.0D, -2.4D, -0.8D, 32.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F);
      this.DrawLeftEar(0.0D, -2.4D, -0.7999D, 32.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F);
      GlStateManager.popMatrix();
   }

   public void DrawRightEar(double x, double y, double z, float u, float v, int uWidth, int vHeight, double width, double height, float tileWidth, float tileHeight) {
      float f = 1.0F / tileWidth;
      float f1 = 1.0F / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      double offset = width * 0.5D;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(x, y + height, z).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
      bufferbuilder.pos(x + width, y + height, z + offset).tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).endVertex();
      bufferbuilder.pos(x + width, y, z + offset).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
      bufferbuilder.pos(x, y, z).tex((double)(u * f), (double)(v * f1)).endVertex();
      tessellator.draw();
   }

   public void DrawLeftEar(double x, double y, double z, float u, float v, int uWidth, int vHeight, double width, double height, float tileWidth, float tileHeight) {
      float f = 1.0F / tileWidth;
      float f1 = 1.0F / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      double offset = width * 0.5D;
      double offset2 = -width;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(x, y + height, z).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
      bufferbuilder.pos(x + offset2, y + height, z + offset).tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).endVertex();
      bufferbuilder.pos(x + offset2, y, z + offset).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
      bufferbuilder.pos(x, y, z).tex((double)(u * f), (double)(v * f1)).endVertex();
      tessellator.draw();
   }
}
