package crafttweaker.mc1120.util;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.util.IAxisAlignedBB;
import crafttweaker.api.world.IVector3d;
import net.minecraft.util.math.AxisAlignedBB;

public class MCAxisAlignedBB implements IAxisAlignedBB {
   private final AxisAlignedBB aabb;

   public MCAxisAlignedBB(AxisAlignedBB aabb) {
      this.aabb = aabb;
   }

   public Object getInternal() {
      return this.aabb;
   }

   public double getMinX() {
      return this.aabb.minX;
   }

   public double getMinY() {
      return this.aabb.minY;
   }

   public double getMinZ() {
      return this.aabb.minZ;
   }

   public double getMaxX() {
      return this.aabb.maxX;
   }

   public double getMaxY() {
      return this.aabb.maxY;
   }

   public double getMaxZ() {
      return this.aabb.maxY;
   }

   public IVector3d getCenter() {
      return CraftTweakerMC.getIVector3d(this.aabb.getCenter());
   }

   public IAxisAlignedBB contract(double x, double y, double z) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.contract(x, y, z));
   }

   public IAxisAlignedBB expand(double x, double y, double z) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.expand(x, y, z));
   }

   public IAxisAlignedBB grow(double value) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.grow(value));
   }

   public IAxisAlignedBB grow(double x, double y, double z) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.grow(x, y, z));
   }

   public IAxisAlignedBB shrink(double value) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.shrink(value));
   }

   public IAxisAlignedBB intersect(IAxisAlignedBB other) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.intersect(CraftTweakerMC.getAxisAlignedBB(other)));
   }

   public boolean intersects(IAxisAlignedBB other) {
      return this.aabb.intersects(CraftTweakerMC.getAxisAlignedBB(other));
   }

   public boolean contains(IVector3d vec) {
      return this.aabb.contains(CraftTweakerMC.getVec3d(vec));
   }

   public IAxisAlignedBB union(IAxisAlignedBB other) {
      return CraftTweakerMC.getIAxisAlignedBB(this.aabb.union(CraftTweakerMC.getAxisAlignedBB(other)));
   }
}
