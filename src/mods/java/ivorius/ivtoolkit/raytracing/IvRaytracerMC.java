package ivorius.ivtoolkit.raytracing;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

public class IvRaytracerMC {
   public IvRaytracerMC() {
   }

   public static List<IvRaytracedIntersection> getIntersections(List<IvRaytraceableObject> objects, Entity entity) {
      double x = entity.posX;
      double y = entity.posY + (double)entity.getEyeHeight();
      double z = entity.posZ;
      Vec3d lookVec = entity.getLookVec();
      return IvRaytracer.getIntersections(objects, x, y, z, lookVec.x, lookVec.y, lookVec.z);
   }

   public static IvRaytracedIntersection getFirstIntersection(List<IvRaytraceableObject> objects, Entity entity) {
      double x = entity.posX;
      double y = entity.posY + (double)entity.getEyeHeight();
      double z = entity.posZ;
      Vec3d lookVec = entity.getLookVec();
      List<IvRaytracedIntersection> intersections = IvRaytracer.getIntersections(objects, x, y, z, lookVec.x, lookVec.y, lookVec.z);
      return IvRaytracer.findFirstIntersection(intersections, x, y, z, lookVec.x, lookVec.y, lookVec.z);
   }

   public static void drawStandardOutlinesFromTileEntity(List<IvRaytraceableObject> objects, double d, double d1, double d2, TileEntity tileEntity) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)d - (float)tileEntity.getPos().getX(), (float)d1 - (float)tileEntity.getPos().getY(), (float)d2 - (float)tileEntity.getPos().getZ());
      IvRaytracer.drawStandardOutlines(objects);
      GlStateManager.popMatrix();
   }
}
