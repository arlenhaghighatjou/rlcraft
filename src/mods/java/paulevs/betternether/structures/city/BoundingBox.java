package paulevs.betternether.structures.city;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BoundingBox {
   int x1;
   int x2;
   int z1;
   int z2;

   public BoundingBox(int x1, int z1, int x2, int z2) {
      this.x1 = x1;
      this.x2 = x2;
      this.z1 = z1;
      this.z2 = z2;
   }

   public BoundingBox(BlockPos size, int offsetX, int offsetZ) {
      this.x1 = offsetX;
      this.x2 = this.x1 + size.getX();
      this.z1 = offsetZ;
      this.z2 = this.z1 + size.getZ();
   }

   public BoundingBox(BlockPos size) {
      this.x1 = 0;
      this.x2 = size.getX();
      this.z1 = 0;
      this.z2 = size.getZ();
   }

   public boolean isColliding(BoundingBox bb) {
      boolean colX = bb.x1 < this.x2 && this.x1 < bb.x2;
      boolean colZ = bb.z1 < this.z2 && this.z1 < bb.z2;
      return colX && colZ;
   }

   public BoundingBox offset(BlockPos offset) {
      return new BoundingBox(this.x1 + offset.getX(), this.z1 + offset.getZ(), this.x2 + offset.getX(), this.z2 + offset.getZ());
   }

   public BoundingBox offsetNegative(BlockPos offset) {
      return new BoundingBox(this.x1 - offset.getX(), this.z1 - offset.getZ(), this.x2 - offset.getX(), this.z2 - offset.getZ());
   }

   public String toString() {
      return this.x1 + " " + this.z1 + " " + this.x2 + " " + this.z2;
   }

   public void rotate(Rotation rotation) {
      BlockPos start = new BlockPos(this.x1, 0, this.z1);
      BlockPos end = new BlockPos(this.x2, 0, this.z2);
      start = start.rotate(rotation);
      end = end.rotate(rotation);
      int nx1 = Math.min(start.getX(), end.getX());
      int nx2 = Math.max(start.getX(), end.getX());
      int nz1 = Math.min(start.getZ(), end.getZ());
      int nz2 = Math.max(start.getZ(), end.getZ());
      this.x1 = 0;
      this.z1 = 0;
      this.x2 = nx2 - nx1;
      this.z2 = nz2 - nz1;
   }

   public BlockPos getCenter() {
      return new BlockPos((double)(this.x2 + this.x1) * 0.5D, 0.0D, (double)(this.z2 + this.z1) * 0.5D);
   }

   public int getSideX() {
      return this.x2 - this.x1;
   }

   public int getSideZ() {
      return this.z2 - this.z1;
   }

   public int getMinX() {
      return this.x1;
   }

   public int getMaxX() {
      return this.x2;
   }

   public int getMinZ() {
      return this.z1;
   }

   public int getMaxZ() {
      return this.z2;
   }
}
