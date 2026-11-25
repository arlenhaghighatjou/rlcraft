package ivorius.ivtoolkit.rendering.grid;

import ivorius.ivtoolkit.blocks.BlockArea;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AreaRenderer {
   public AreaRenderer() {
   }

   public static void renderAreaLined(BlockArea area, float sizeP) {
      renderArea(area, true, false, sizeP);
   }

   public static void renderArea(BlockArea area, boolean lined, boolean insides, float sizeP) {
      drawCuboid(area.getLowerCorner(), area.getHigherCorner().add(1, 1, 1), lined, insides, sizeP);
   }

   @SideOnly(Side.CLIENT)
   private static void drawCuboid(BlockPos min, BlockPos max, boolean lined, boolean insides, float sizeP) {
      float width2 = ((float)max.getX() - (float)min.getX()) * 0.5F;
      float height2 = ((float)max.getY() - (float)min.getY()) * 0.5F;
      float length2 = ((float)max.getZ() - (float)min.getZ()) * 0.5F;
      double centerX = (double)((float)min.getX() + width2);
      double centerY = (double)((float)min.getY() + height2);
      double centerZ = (double)((float)min.getZ() + length2);
      int sizeCE = insides ? -1 : 1;
      GlStateManager.pushMatrix();
      GlStateManager.translate(centerX, centerY, centerZ);
      if (lined) {
         GlStateManager.disableTexture2D();
         drawLineCuboid(Tessellator.getInstance().getBuffer(), width2 + sizeP, height2 + sizeP, length2 + sizeP, 1.0F);
         GlStateManager.enableTexture2D();
      } else {
         drawCuboid(Tessellator.getInstance().getBuffer(), width2 * (float)sizeCE + sizeP, height2 * (float)sizeCE + sizeP, length2 * (float)sizeCE + sizeP, 1.0F);
      }

      GlStateManager.popMatrix();
   }

   public static void drawCuboid(BufferBuilder renderer, float sizeX, float sizeY, float sizeZ, float in) {
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).tex(0.0D, 1.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)sizeZ).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)sizeZ).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)sizeZ).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)sizeZ).tex(0.0D, 1.0D).endVertex();
      renderer.pos((double)(-sizeX), (double)(-sizeY * in), (double)(-sizeZ * in)).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX), (double)(-sizeY * in), (double)(sizeZ * in)).tex(0.0D, 1.0D).endVertex();
      renderer.pos((double)(-sizeX), (double)(sizeY * in), (double)(sizeZ * in)).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)(-sizeX), (double)(sizeY * in), (double)(-sizeZ * in)).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)sizeX, (double)(-sizeY * in), (double)(-sizeZ * in)).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)sizeX, (double)(sizeY * in), (double)(-sizeZ * in)).tex(0.0D, 1.0D).endVertex();
      renderer.pos((double)sizeX, (double)(sizeY * in), (double)(sizeZ * in)).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)sizeX, (double)(-sizeY * in), (double)(sizeZ * in)).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)sizeY, (double)(-sizeZ * in)).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)sizeY, (double)(sizeZ * in)).tex(0.0D, 1.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)sizeY, (double)(sizeZ * in)).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)sizeY, (double)(-sizeZ * in)).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).tex(1.0D, 1.0D).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).tex(0.0D, 1.0D).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void drawLineCuboid(BufferBuilder renderer, float sizeX, float sizeY, float sizeZ, float in) {
      renderer.begin(3, DefaultVertexFormats.POSITION);
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      Tessellator.getInstance().draw();
      renderer.begin(1, DefaultVertexFormats.POSITION);
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(-sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      renderer.pos((double)(sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      Tessellator.getInstance().draw();
   }
}
