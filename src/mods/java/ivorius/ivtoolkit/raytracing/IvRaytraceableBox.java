package ivorius.ivtoolkit.raytracing;

import java.util.List;

public class IvRaytraceableBox extends IvRaytraceableObject {
   private double x;
   private double y;
   private double z;
   private double width;
   private double height;
   private double depth;
   private IvRaytraceableAxisAlignedSurface[] surfaces;

   public IvRaytraceableBox(Object userInfo, double x, double y, double z, double width, double height, double depth) {
      super(userInfo);
      this.x = x;
      this.y = y;
      this.z = z;
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.surfaces = new IvRaytraceableAxisAlignedSurface[6];
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }

   public double getDepth() {
      return this.depth;
   }

   public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir) {
      for(IvRaytraceableAxisAlignedSurface surface : this.surfaces) {
         surface.addRaytracedIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
      }

   }
}
