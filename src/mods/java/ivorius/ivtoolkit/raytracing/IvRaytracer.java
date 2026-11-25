package ivorius.ivtoolkit.raytracing;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class IvRaytracer {
   public IvRaytracer() {
   }

   public static List<IvRaytracedIntersection> getIntersections(List<IvRaytraceableObject> objects, double x, double y, double z, double xDir, double yDir, double zDir) {
      ArrayList<IvRaytracedIntersection> hitPoints = new ArrayList();

      for(IvRaytraceableObject object : objects) {
         object.addIntersectionsForLineToList(hitPoints, x, y, z, xDir, yDir, zDir);
      }

      return hitPoints;
   }

   public static IvRaytracedIntersection findFirstIntersection(List<IvRaytracedIntersection> intersections, double x, double y, double z, double xDir, double yDir, double zDir) {
      IvRaytracedIntersection firstPoint = null;
      double firstDistanceSQ = -1.0D;

      for(IvRaytracedIntersection point : intersections) {
         double pointDistSQ = (x - point.getX()) * (x - point.getX()) + (y - point.getY()) * (y - point.getY()) + (z - point.getZ()) * (z - point.getZ());
         boolean forwards = point.getX() - x > 0.0D == xDir > 0.0D && point.getY() - y > 0.0D == yDir > 0.0D && point.getZ() - z > 0.0D == zDir > 0.0D;
         if (forwards && (firstDistanceSQ < 0.0D || pointDistSQ < firstDistanceSQ)) {
            firstPoint = point;
            firstDistanceSQ = pointDistSQ;
         }
      }

      return firstPoint;
   }

   public static IvRaytracedIntersection getFirstIntersection(List<IvRaytraceableObject> objects, double x, double y, double z, double xDir, double yDir, double zDir) {
      ArrayList<IvRaytracedIntersection> intersections = new ArrayList();

      for(IvRaytraceableObject object : objects) {
         object.addIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);
      }

      return findFirstIntersection(intersections, x, y, z, xDir, yDir, zDir);
   }

   public static void drawStandardOutlines(List<IvRaytraceableObject> objects) {
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glLineWidth(2.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      EaglercraftRandom r = new EaglercraftRandom(0L);

      for(IvRaytraceableObject object : objects) {
         int color = r.nextInt();
         GlStateManager.color((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color >> 0 & 255) / 255.0F, 0.8F);
         object.drawOutlines();
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}
