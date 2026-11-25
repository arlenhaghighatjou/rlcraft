package com.charles445.simpledifficulty.util;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil {
   public RenderUtil() {
   }

   public static void drawTexturedModalRect(float x, float y, int texX, int texY, int width, int height) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      double z = 0.0D;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)x, (double)(y + (float)height), z).tex((double)((float)texX * f), (double)((float)(texY + height) * f1)).endVertex();
      bufferbuilder.pos((double)(x + (float)width), (double)(y + (float)height), z).tex((double)((float)(texX + width) * f), (double)((float)(texY + height) * f1)).endVertex();
      bufferbuilder.pos((double)(x + (float)width), (double)y, z).tex((double)((float)(texX + width) * f), (double)((float)texY * f1)).endVertex();
      bufferbuilder.pos((double)x, (double)y, z).tex((double)((float)texX * f), (double)((float)texY * f1)).endVertex();
      tessellator.draw();
   }
}
