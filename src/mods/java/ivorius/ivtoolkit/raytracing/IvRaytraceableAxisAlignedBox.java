package ivorius.ivtoolkit.raytracing;

import java.util.List;
import net.minecraft.util.EnumFacing;

public class IvRaytraceableAxisAlignedBox extends IvRaytraceableObject {
   private double x;
   private double y;
   private double z;
   private double width;
   private double height;
   private double depth;
   private IvRaytraceableAxisAlignedSurface[] surfaces;

   public IvRaytraceableAxisAlignedBox(Object userInfo, double x, double y, double z, double width, double height, double depth) {
      super(userInfo);
      this.x = x;
      this.y = y;
      this.z = z;
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.surfaces = new IvRaytraceableAxisAlignedSurface[6];
      this.surfaces[0] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.WEST, y, z, 0.0D, height, depth, x);
      this.surfaces[1] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.EAST, y, z, 0.0D, height, depth, x + width);
      this.surfaces[2] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.DOWN, y, z, width, 0.0D, depth, x);
      this.surfaces[3] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.UP, y + height, z, width, 0.0D, depth, x);
      this.surfaces[4] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.NORTH, y, z, width, height, 0.0D, x);
      this.surfaces[5] = new IvRaytraceableAxisAlignedSurface(userInfo, EnumFacing.SOUTH, y, z + depth, width, height, 0.0D, x);
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

   public void drawOutlines() {
      super.drawOutlines();

      for(IvRaytraceableObject object : this.surfaces) {
         object.drawOutlines();
      }

   }

   public String toString() {
      return "IvRaytraceableAxisAlignedBox{ " + this.x + ", " + this.y + ", " + this.z + " -> " + this.width + " x " + this.height + " x " + this.depth + "}";
   }
}
