package com.lycanitesmobs.client.obj;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VBOModel extends TessellatorModel {
   public VBOModel(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public void renderGroupImpl(ObjObject obj, Vector4f color, Vector2f textureOffset, VertexFormat vertexFormat) {
      GlStateManager.color(color.x, color.y, color.z, color.w);
      boolean hasTextureOffset = textureOffset.x != 0.0F || textureOffset.y != 0.0F;
      if (hasTextureOffset) {
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.translate((double)textureOffset.x * 0.01D, (double)(-textureOffset.y) * 0.01D, 0.0D);
         GlStateManager.matrixMode(5888);
      }

      GL15.glBindBuffer(34962, obj.mesh.getVbo());
      GL11.glVertexPointer(3, 5126, 24, 0L);
      GL11.glTexCoordPointer(2, 5126, 24, 12L);
      GL11.glNormalPointer(5120, 24, 20L);
      GL11.glEnableClientState(32884);
      GL11.glEnableClientState(32888);
      GL11.glEnableClientState(32885);
      GL11.glDrawArrays(4, 0, obj.mesh.indices.length);
      GL11.glDisableClientState(32884);
      GL11.glDisableClientState(32888);
      GL11.glDisableClientState(32885);
      GL15.glBindBuffer(34962, 0);
      if (hasTextureOffset) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
