package ivorius.ivtoolkit.rendering.grid;

import ivorius.ivtoolkit.rendering.Icon;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;

public class CubeMesh {
   public CubeMesh() {
   }

   public static void renderSides(Collection<EnumFacing> sides, double x1, double y1, double z1, double x2, double y2, double z2, Icon icon) {
      for(EnumFacing direction : sides) {
         renderSide(direction, x1, y1, z1, x2, y2, z2, icon);
      }

   }

   public static void renderSide(EnumFacing side, double x1, double y1, double z1, double x2, double y2, double z2, Icon icon) {
      switch(side) {
      case NORTH:
         renderNorth(x1, y1, x2, y2, z1, icon);
         break;
      case EAST:
         renderEast(z1, y1, z2, y2, x2, icon);
         break;
      case SOUTH:
         renderSouth(x1, y1, x2, y2, z2, icon);
         break;
      case WEST:
         renderWest(z1, y1, z2, y2, x1, icon);
         break;
      case UP:
         renderUp(x1, z1, x2, z2, y2, icon);
         break;
      case DOWN:
         renderDown(x1, z1, x2, z2, y1, icon);
         break;
      default:
         throw new IllegalArgumentException();
      }

   }

   public static void renderNorth(double x1, double y1, double x2, double y2, double z, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x1, y1, z).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x1, y2, z).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x2, y2, z).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x2, y1, z).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
   }

   public static void renderEast(double z1, double y1, double z2, double y2, double x, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x, y1, z1).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x, y2, z1).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x, y2, z2).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x, y1, z2).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
   }

   public static void renderSouth(double x1, double y1, double x2, double y2, double z, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x1, y1, z).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x2, y1, z).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x2, y2, z).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x1, y2, z).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
   }

   public static void renderWest(double z1, double y1, double z2, double y2, double x, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x, y1, z1).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x, y1, z2).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x, y2, z2).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x, y2, z1).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
   }

   public static void renderUp(double x1, double z1, double x2, double z2, double y, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x1, y, z1).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x1, y, z2).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x2, y, z2).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x2, y, z1).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
   }

   public static void renderDown(double x1, double z1, double x2, double z2, double y, Icon icon) {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.pos(x1, y, z1).tex((double)icon.getMinU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x2, y, z1).tex((double)icon.getMaxU(), (double)icon.getMinV()).endVertex();
      renderer.pos(x2, y, z2).tex((double)icon.getMaxU(), (double)icon.getMaxV()).endVertex();
      renderer.pos(x1, y, z2).tex((double)icon.getMinU(), (double)icon.getMaxV()).endVertex();
   }
}
