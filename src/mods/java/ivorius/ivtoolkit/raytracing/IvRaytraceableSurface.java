package ivorius.ivtoolkit.raytracing;

import java.util.List;

public class IvRaytraceableSurface extends IvRaytraceableObject {
   public double x;
   public double y;
   public double z;
   public double x1;
   public double y1;
   public double z1;
   public double x2;
   public double y2;
   public double z2;

   public IvRaytraceableSurface(Object userInfo, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
      super(userInfo);
      this.x = x;
      this.y = y;
      this.z = z;
      this.x1 = x1;
      this.y1 = y1;
      this.z1 = z1;
      this.x2 = x2;
      this.y2 = y2;
      this.z2 = z2;
   }

   public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir) {
   }
}
