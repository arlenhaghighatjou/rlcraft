package reborncore.common.misc.vecmath;

import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class Vecs3dCube {
   private Vecs3d min;
   private Vecs3d max;

   public Vecs3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this(minX, minY, minZ, maxX, maxY, maxZ, (World)null);
   }

   public Vecs3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, World world) {
      this(new Vecs3d(minX, minY, minZ, world), new Vecs3d(maxX, maxY, maxZ, world));
   }

   public Vecs3dCube(Vecs3d a, Vecs3d b) {
      World w = a.getWorld();
      if (w == null) {
         w = b.getWorld();
      }

      this.min = a;
      this.max = b;
      this.fix();
   }

   public Vecs3dCube(AxisAlignedBB aabb) {
      this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
   }

   public Vecs3d getMin() {
      return this.min;
   }

   public Vecs3d getMax() {
      return this.max;
   }

   public Vecs3d getCenter() {
      return new Vecs3d((this.getMinX() + this.getMaxX()) / 2.0D, (this.getMinY() + this.getMaxY()) / 2.0D, (this.getMinZ() + this.getMaxZ()) / 2.0D, this.getMin().getWorld());
   }

   public double getMinX() {
      return this.min.getX();
   }

   public double getMinY() {
      return this.min.getY();
   }

   public double getMinZ() {
      return this.min.getZ();
   }

   public double getMaxX() {
      return this.max.getX();
   }

   public double getMaxY() {
      return this.max.getY();
   }

   public double getMaxZ() {
      return this.max.getZ();
   }

   public AxisAlignedBB toAABB() {
      return new AxisAlignedBB(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
   }

   public Vecs3dCube clone() {
      return new Vecs3dCube(this.min.clone(), this.max.clone());
   }

   public Vecs3dCube expand(double size) {
      this.min.sub(size, size, size);
      this.max.add(size, size, size);
      return this;
   }

   public Vecs3dCube fix() {
      Vecs3d a = this.min.clone();
      Vecs3d b = this.max.clone();
      double minX = Math.min(a.getX(), b.getX());
      double minY = Math.min(a.getY(), b.getY());
      double minZ = Math.min(a.getZ(), b.getZ());
      double maxX = Math.max(a.getX(), b.getX());
      double maxY = Math.max(a.getY(), b.getY());
      double maxZ = Math.max(a.getZ(), b.getZ());
      this.min = new Vecs3d(minX, minY, minZ, a.w);
      this.max = new Vecs3d(maxX, maxY, maxZ, b.w);
      return this;
   }

   public Vecs3dCube add(double x, double y, double z) {
      this.min.add(x, y, z);
      this.max.add(x, y, z);
      return this;
   }

   public static final Vecs3dCube merge(List<Vecs3dCube> cubes) {
      double minx = Double.MAX_VALUE;
      double miny = Double.MAX_VALUE;
      double minz = Double.MAX_VALUE;
      double maxx = Double.MIN_VALUE;
      double maxy = Double.MIN_VALUE;
      double maxz = Double.MIN_VALUE;

      for(Vecs3dCube c : cubes) {
         minx = Math.min(minx, c.getMinX());
         miny = Math.min(miny, c.getMinY());
         minz = Math.min(minz, c.getMinZ());
         maxx = Math.max(maxx, c.getMaxX());
         maxy = Math.max(maxy, c.getMaxY());
         maxz = Math.max(maxz, c.getMaxZ());
      }

      return cubes.size() == 0 ? new Vecs3dCube(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D) : new Vecs3dCube(minx, miny, minz, maxx, maxy, maxz);
   }

   public int hashCode() {
      return this.min.hashCode() << 8 + this.max.hashCode();
   }
}
