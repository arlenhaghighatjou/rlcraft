package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.common.utils.math.Rotation;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class AlignedBox {
   public float minX;
   public float minY;
   public float minZ;
   public float maxX;
   public float maxY;
   public float maxZ;

   public AlignedBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public AlignedBox(AxisAlignedBB box) {
      this((float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
   }

   public AlignedBox() {
      this(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public AlignedBox(AlignedBox cube) {
      this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
   }

   public void add(float x, float y, float z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
   }

   public void sub(float x, float y, float z) {
      this.minX -= x;
      this.minY -= y;
      this.minZ -= z;
      this.maxX -= x;
      this.maxY -= y;
      this.maxZ -= z;
   }

   public void add(Vector3d vec) {
      this.add((float)vec.x, (float)vec.y, (float)vec.z);
   }

   public void sub(Vector3d vec) {
      this.sub((float)vec.x, (float)vec.y, (float)vec.z);
   }

   public void add(Vec3i vec) {
      this.add((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
   }

   public void sub(Vec3i vec) {
      this.sub((float)vec.getX(), (float)vec.getY(), (float)vec.getZ());
   }

   public void scale(float scale) {
      this.minX *= scale;
      this.minY *= scale;
      this.minZ *= scale;
      this.maxX *= scale;
      this.maxY *= scale;
      this.maxZ *= scale;
   }

   public Vector3d getSize() {
      return new Vector3d((double)(this.maxX - this.minX), (double)(this.maxY - this.minY), (double)(this.maxZ - this.minZ));
   }

   public Vector3d getCenter() {
      return new Vector3d((double)(this.maxX + this.minX) * 0.5D, (double)(this.maxY + this.minY) * 0.5D, (double)(this.maxZ + this.minZ) * 0.5D);
   }

   public String toString() {
      return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public Vector3f getCorner(BoxCorner corner) {
      return new Vector3f(this.getValueOfFacing(corner.x), this.getValueOfFacing(corner.y), this.getValueOfFacing(corner.z));
   }

   public AxisAlignedBB getBB() {
      return new AxisAlignedBB((double)this.minX, (double)this.minY, (double)this.minZ, (double)this.maxX, (double)this.maxY, (double)this.maxZ);
   }

   public AxisAlignedBB getBB(BlockPos pos) {
      return new AxisAlignedBB((double)(this.minX + (float)pos.getX()), (double)(this.minY + (float)pos.getY()), (double)(this.minZ + (float)pos.getZ()), (double)(this.maxX + (float)pos.getX()), (double)(this.maxY + (float)pos.getY()), (double)(this.maxZ + (float)pos.getZ()));
   }

   public void rotate(Rotation rotation, Vector3f center) {
      Vector3f low = new Vector3f(this.minX, this.minY, this.minZ);
      Vector3f high = new Vector3f(this.maxX, this.maxY, this.maxZ);
      low.sub(center);
      high.sub(center);
      rotation.getMatrix().transform(low);
      rotation.getMatrix().transform(high);
      low.add(center);
      high.add(center);
      this.set(low.x, low.y, low.z, high.x, high.y, high.z);
   }

   public void rotate(EnumFacing facing, Vector3f center) {
      Matrix3f matrix = new Matrix3f();
      if (facing.getAxis() == Axis.X) {
         facing = facing.getOpposite();
      }

      matrix.rotY((float)Math.toRadians((double)facing.getHorizontalAngle()));
      this.rotate(matrix, center);
   }

   public void rotate(Matrix3f matrix, Vector3f center) {
      Vector3f low = new Vector3f(this.minX, this.minY, this.minZ);
      Vector3f high = new Vector3f(this.maxX, this.maxY, this.maxZ);
      low.sub(center);
      high.sub(center);
      matrix.transform(low);
      matrix.transform(high);
      low.add(center);
      high.add(center);
      this.set(low.x, low.y, low.z, high.x, high.y, high.z);
   }

   public void set(float x, float y, float z, float x2, float y2, float z2) {
      this.minX = Math.min(x, x2);
      this.minY = Math.min(y, y2);
      this.minZ = Math.min(z, z2);
      this.maxX = Math.max(x, x2);
      this.maxY = Math.max(y, y2);
      this.maxZ = Math.max(z, z2);
   }

   public BlockPos getOffset() {
      return new BlockPos((double)this.minX, (double)this.minY, (double)this.minZ);
   }

   public float getValueOfFacing(EnumFacing facing) {
      switch(facing) {
      case EAST:
         return this.maxX;
      case WEST:
         return this.minX;
      case UP:
         return this.maxY;
      case DOWN:
         return this.minY;
      case SOUTH:
         return this.maxZ;
      case NORTH:
         return this.minZ;
      default:
         return 0.0F;
      }
   }

   public float getSize(Axis axis) {
      switch(axis) {
      case X:
         return this.maxX - this.minX;
      case Y:
         return this.maxY - this.minY;
      case Z:
         return this.maxZ - this.minZ;
      default:
         return 0.0F;
      }
   }

   public void setMin(Axis axis, float value) {
      switch(axis) {
      case X:
         this.minX = value;
         break;
      case Y:
         this.minY = value;
         break;
      case Z:
         this.minZ = value;
      }

   }

   public float getMin(Axis axis) {
      switch(axis) {
      case X:
         return this.minX;
      case Y:
         return this.minY;
      case Z:
         return this.minZ;
      default:
         return 0.0F;
      }
   }

   public void setMax(Axis axis, float value) {
      switch(axis) {
      case X:
         this.maxX = value;
         break;
      case Y:
         this.maxY = value;
         break;
      case Z:
         this.maxZ = value;
      }

   }

   public float getMax(Axis axis) {
      switch(axis) {
      case X:
         return this.maxX;
      case Y:
         return this.maxY;
      case Z:
         return this.maxZ;
      default:
         return 0.0F;
      }
   }

   public void shrink(Axis axis, float value) {
      value = value / 2.0F;
      this.setMin(axis, this.getMin(axis) + value);
      this.setMax(axis, this.getMax(axis) - value);
   }
}
