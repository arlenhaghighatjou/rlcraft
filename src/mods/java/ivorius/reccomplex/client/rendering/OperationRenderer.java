package ivorius.reccomplex.client.rendering;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.rendering.grid.AreaRenderer;
import ivorius.ivtoolkit.rendering.grid.CubeMesh;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache.CachedQuadLevel;
import ivorius.reccomplex.utils.Icons;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class OperationRenderer {
   public OperationRenderer() {
   }

   public static void applyTransformVisual(AxisAlignedTransform2D transform2D, float[] size) {
      if (transform2D.getRotation() % 2 == 1) {
         GlStateManager.translate(size[2] * 0.5F, 0.0F, size[0] * 0.5F);
      } else {
         GlStateManager.translate(size[0] * 0.5F, 0.0F, size[2] * 0.5F);
      }

      GlStateManager.rotate(-90.0F * (float)transform2D.getRotation(), 0.0F, 1.0F, 0.0F);
      if (transform2D.isMirrorX()) {
         GlStateManager.scale(-1.0F, 1.0F, 1.0F);
      }

      GlStateManager.translate(-size[0] * 0.5F, 0.0F, -size[2] * 0.5F);
   }

   public static void renderGridQuadCache(GridQuadCache<?> cached, AxisAlignedTransform2D transform, BlockPos lowerCoord, int ticks, float partialTicks) {
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.5F);
      GlStateManager.disableCull();
      ResourceLocation curTex = (ResourceLocation)Icons.frame(SelectionRenderer.LATTICE_TEXTURE, ((float)ticks + partialTicks) * 0.75F);
      Minecraft.getMinecraft().renderEngine.bindTexture(curTex);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)lowerCoord.getX(), (float)lowerCoord.getY(), (float)lowerCoord.getZ());
      applyTransformVisual(transform, cached.getSize());
      Tessellator tessellator = Tessellator.getInstance();
      tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);

      for(CachedQuadLevel<?> cachedQuadLevel : cached) {
         EnumFacing direction = cachedQuadLevel.direction;
         float zLevel = cachedQuadLevel.zLevel + 0.01F * (float)(direction.getFrontOffsetX() + direction.getFrontOffsetY() + direction.getFrontOffsetZ());
         FloatBuffer quads = cachedQuadLevel.quads;

         while(quads.position() < quads.limit() - 3) {
            float minX = quads.get();
            float minY = quads.get();
            float maxX = quads.get();
            float maxY = quads.get();
            float[] minAxes = GridQuadCache.getNormalAxes(direction, new float[]{zLevel, minX, minY});
            float[] maxAxes = GridQuadCache.getNormalAxes(direction, new float[]{zLevel, maxX, maxY});
            CubeMesh.renderSide(direction, (double)minAxes[0], (double)minAxes[1], (double)minAxes[2], (double)maxAxes[0], (double)maxAxes[1], (double)maxAxes[2], Icons.from(minX, minY, maxX, maxY));
         }

         quads.position(0);
      }

      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableCull();
      GlStateManager.alphaFunc(516, 0.002F);
      GlStateManager.disableBlend();
   }

   @Nullable
   public static BlockArea blockAreaFromSize(BlockPos lowerCoord, int[] size) {
      return size[0] > 0 && size[1] > 0 && size[2] > 0 ? BlockArea.areaFromSize(lowerCoord, size) : null;
   }

   public static void renderBoundingBox(@Nullable BlockArea area, int ticks, float partialTicks) {
      if (area != null) {
         GL11.glLineWidth(3.0F);
         GlStateManager.color(0.8F, 0.8F, 1.0F);
         AreaRenderer.renderAreaLined(area, 0.0232F);
         GlStateManager.enableBlend();
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 1.0E-4F);
         ResourceLocation curTex = (ResourceLocation)Icons.frame(SelectionRenderer.TEXTURE, ((float)ticks + partialTicks) * 0.75F);
         Minecraft.getMinecraft().renderEngine.bindTexture(curTex);
         GlStateManager.color(0.6F, 0.6F, 0.8F, 0.3F);
         AreaRenderer.renderArea(area, false, true, 0.0132F);
         GlStateManager.color(0.8F, 0.8F, 1.0F, 0.5F);
         AreaRenderer.renderArea(area, false, false, 0.0132F);
         GlStateManager.alphaFunc(516, 0.002F);
         GlStateManager.disableBlend();
      }
   }
}
