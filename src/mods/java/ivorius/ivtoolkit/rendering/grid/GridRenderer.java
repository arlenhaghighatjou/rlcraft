package ivorius.ivtoolkit.rendering.grid;

import ivorius.ivtoolkit.rendering.IvRenderHelper;
import ivorius.ivtoolkit.tools.BufferBuilderAccessor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

public class GridRenderer {
   public GridRenderer() {
   }

   public static void renderGrid(int lines, float spacing, float lineLength, float lineWidth) {
      Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION);

      for(int x = -lines; x <= lines; ++x) {
         for(int z = -lines; z <= lines; ++z) {
            renderLine((float)x * spacing, -lineLength * 0.5F, (float)z * spacing, EnumFacing.UP, lineLength, lineWidth);
         }
      }

      for(int x = -lines; x <= lines; ++x) {
         for(int y = -lines; y <= lines; ++y) {
            renderLine((float)x * spacing, (float)y * spacing, -lineLength * 0.5F, EnumFacing.SOUTH, lineLength, lineWidth);
         }
      }

      for(int y = -lines; y <= lines; ++y) {
         for(int z = -lines; z <= lines; ++z) {
            renderLine(-lineLength * 0.5F, (float)y * spacing, (float)z * spacing, EnumFacing.EAST, lineLength, lineWidth);
         }
      }

      Tessellator.getInstance().draw();
   }

   public static void renderLine(float x, float y, float z, EnumFacing direction, float length, float size) {
      float xDir = (float)direction.getFrontOffsetX() * length;
      float yDir = (float)direction.getFrontOffsetY() * length;
      float zDir = (float)direction.getFrontOffsetZ() * length;
      if (xDir == 0.0F) {
         xDir = size;
      } else {
         x = (float)((double)x + (double)xDir * 0.5D);
      }

      if (yDir == 0.0F) {
         yDir = size;
      } else {
         y = (float)((double)y + (double)yDir * 0.5D);
      }

      if (zDir == 0.0F) {
         zDir = size;
      } else {
         z = (float)((double)z + (double)zDir * 0.5D);
      }

      BufferBuilder renderer = Tessellator.getInstance().getBuffer();
      BufferBuilderAccessor.addTranslation(renderer, (double)x, (double)y, (double)z);
      IvRenderHelper.renderCuboid(renderer, xDir, yDir, zDir, 1.0F);
      BufferBuilderAccessor.addTranslation(renderer, (double)(-x), (double)(-y), (double)(-z));
   }
}
