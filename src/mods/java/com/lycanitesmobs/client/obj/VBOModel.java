package com.lycanitesmobs.client.obj;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;

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

      GlStateManager.callList(obj.mesh.getVbo());
      if (hasTextureOffset) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
