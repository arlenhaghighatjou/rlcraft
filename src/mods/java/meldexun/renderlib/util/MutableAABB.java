package meldexun.renderlib.util;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class MutableAABB {
   private double minX;
   private double minY;
   private double minZ;
   private double maxX;
   private double maxY;
   private double maxZ;

   public MutableAABB() {
      this(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   }

   public MutableAABB(MutableAABB aabb) {
      this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
   }

   public MutableAABB(AxisAlignedBB aabb) {
      this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
   }

   public MutableAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.set(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public AxisAlignedBB toVanillaAABB() {
      return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public MutableAABB set(MutableAABB aabb) {
      return this.set(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
   }

   public MutableAABB set(AxisAlignedBB aabb) {
      return this.set(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
   }

   public MutableAABB set(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      return this;
   }

   public MutableAABB grow(double d) {
      return this.grow(d, d, d);
   }

   public MutableAABB grow(Vec3d vec) {
      return this.grow(vec.x, vec.y, vec.z);
   }

   public MutableAABB grow(double x, double y, double z) {
      return this.grow(x, y, z, x, y, z);
   }

   public MutableAABB grow(double x0, double y0, double z0, double x1, double y1, double z1) {
      this.minX -= x0;
      this.minY -= y0;
      this.minZ -= z0;
      this.maxX += x1;
      this.maxY += y1;
      this.maxZ += z1;
      return this;
   }

   public MutableAABB offset(double x, double y, double z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
      return this;
   }

   public MutableAABB expand(Vec3d vec) {
      return this.expand(vec.x, vec.y, vec.z);
   }

   public MutableAABB expand(Vec3d vec, double d) {
      return this.expand(vec.x, vec.y, vec.z, d);
   }

   public MutableAABB expand(double x, double y, double z, double d) {
      return this.expand(x * d, y * d, z * d);
   }

   public MutableAABB expand(double x, double y, double z) {
      if (x < 0.0D) {
         this.minX += x;
      } else {
         this.maxX += x;
      }

      if (y < 0.0D) {
         this.minY += y;
      } else {
         this.maxY += y;
      }

      if (z < 0.0D) {
         this.minZ += z;
      } else {
         this.maxZ += z;
      }

      return this;
   }

   public boolean isVisible(Frustum frustum) {
      return frustum.isAABBInFrustum(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public boolean isVisible(ICamera frustum) {
      return frustum instanceof net.minecraft.client.renderer.culling.Frustum ? ((net.minecraft.client.renderer.culling.Frustum)frustum).isBoxInFrustum(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ) : frustum.isBoundingBoxInFrustum(this.toVanillaAABB());
   }

   public double sizeX() {
      return this.maxX - this.minX;
   }

   public double sizeY() {
      return this.maxY - this.minY;
   }

   public double sizeZ() {
      return this.maxZ - this.minZ;
   }

   public double minX() {
      return this.minX;
   }

   public double minY() {
      return this.minY;
   }

   public double minZ() {
      return this.minZ;
   }

   public double maxX() {
      return this.maxX;
   }

   public double maxY() {
      return this.maxY;
   }

   public double maxZ() {
      return this.maxZ;
   }
}
