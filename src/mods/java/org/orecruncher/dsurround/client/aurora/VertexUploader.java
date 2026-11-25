package org.orecruncher.dsurround.client.aurora;

import java.nio.ByteBuffer;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VertexUploader {
   public VertexUploader() {
   }

   public void draw(WorldRenderer bufferBuilderIn) {
      if (bufferBuilderIn.getVertexCount() > 0) {
         VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
         int i = vertexformat.getNextOffset();
         ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
         List<VertexFormatElement> list = vertexformat.getElements();
         int size = list.size();

         for(int j = 0; j < size; ++j) {
            VertexFormatElement vertexformatelement = (VertexFormatElement)list.get(j);
            vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
         }

         GlStateManager.glDrawArrays(bufferBuilderIn.getDrawMode(), 0, bufferBuilderIn.getVertexCount());

         for(int i1 = 0; i1 < size; ++i1) {
            VertexFormatElement vertexformatelement1 = (VertexFormatElement)list.get(i1);
            vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
         }
      }

   }
}
