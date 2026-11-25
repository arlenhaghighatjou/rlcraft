package net.silentchaos512.lib.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class DimensionalPosition {
   public static final DimensionalPosition ZERO = new DimensionalPosition(0, 0, 0, 0);
   public final int x;
   public final int y;
   public final int z;
   public final int dim;

   public DimensionalPosition(BlockPos pos, int dim) {
      this(pos.getX(), pos.getY(), pos.getZ(), dim);
   }

   public DimensionalPosition(int x, int y, int z, int dim) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.dim = dim;
   }

   public static DimensionalPosition readFromNBT(NBTTagCompound tags) {
      return new DimensionalPosition(tags.getInteger("posX"), tags.getInteger("posY"), tags.getInteger("posZ"), tags.getInteger("dim"));
   }

   public void writeToNBT(NBTTagCompound tags) {
      tags.setInteger("posX", this.x);
      tags.setInteger("posY", this.y);
      tags.setInteger("posZ", this.z);
      tags.setInteger("dim", this.dim);
   }

   public BlockPos toBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public String toString() {
      return String.format("(%d, %d, %d) in dimension %d", this.x, this.y, this.z, this.dim);
   }

   public boolean equals(Object other) {
      if (!(other instanceof DimensionalPosition)) {
         return false;
      } else {
         DimensionalPosition pos = (DimensionalPosition)other;
         return this.x == pos.x && this.y == pos.y && this.z == pos.z && this.dim == pos.dim;
      }
   }
}
