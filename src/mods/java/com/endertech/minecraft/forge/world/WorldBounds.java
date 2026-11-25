package com.endertech.minecraft.forge.world;

import com.endertech.common.IntBounds;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class WorldBounds {
   private final IntBounds x;
   private final IntBounds y;
   private final IntBounds z;

   public static WorldBounds from(IntBounds x, IntBounds y, IntBounds z) {
      return new WorldBounds(x, y, z);
   }

   public static WorldBounds from(BlockPos pos, int radius) {
      return from(IntBounds.from(Integer.valueOf(pos.getX())), IntBounds.from(Integer.valueOf(pos.getY())), IntBounds.from(Integer.valueOf(pos.getZ()))).extend(radius);
   }

   public static WorldBounds from(BlockPos pos1, BlockPos pos2) {
      return from(IntBounds.from(Integer.valueOf(pos1.getX()), Integer.valueOf(pos2.getX())), IntBounds.from(Integer.valueOf(pos1.getY()), Integer.valueOf(pos2.getY())), IntBounds.from(Integer.valueOf(pos1.getZ()), Integer.valueOf(pos2.getZ())));
   }

   public static WorldBounds vertPlane(BlockPos centerPos, int radius, EnumFacing normal) {
      if (normal.getAxis().isVertical()) {
         return from(centerPos, centerPos);
      } else {
         EnumFacing facing = normal.rotateY();
         BlockPos from = new BlockPos(centerPos.offset(facing, -radius).down(radius));
         BlockPos to = new BlockPos(centerPos.offset(facing, radius).up(radius));
         WorldBounds bounds = from(from, to);
         return bounds;
      }
   }

   protected WorldBounds(IntBounds x, IntBounds y, IntBounds z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public IntBounds getX() {
      return this.x;
   }

   public IntBounds getZ() {
      return this.z;
   }

   public IntBounds getY() {
      return this.y;
   }

   public int getHeight() {
      return this.getY().length().intValue() + 1;
   }

   public boolean encloses(BlockPos pos) {
      return this.getX().encloses(Integer.valueOf(pos.getX())) && this.getY().encloses(Integer.valueOf(pos.getY())) && this.getZ().encloses(Integer.valueOf(pos.getZ()));
   }

   public WorldBounds offset(int x, int y, int z) {
      return from(this.getX().offset(Integer.valueOf(x)), this.getY().offset(Integer.valueOf(y)), this.getZ().offset(Integer.valueOf(z)));
   }

   public WorldBounds extend(int x, int y, int z) {
      return from(this.getX().extend(Integer.valueOf(x)), this.getY().extend(Integer.valueOf(y)), this.getZ().extend(Integer.valueOf(z)));
   }

   public WorldBounds extend(int value) {
      return this.extend(value, value, value);
   }

   public WorldBounds extend(EnumFacing facing, int value) {
      IntBounds x = this.getX().offset(Integer.valueOf(facing.getFrontOffsetX() * value)).unite(this.getX());
      IntBounds y = this.getY().offset(Integer.valueOf(facing.getFrontOffsetY() * value)).unite(this.getY());
      IntBounds z = this.getZ().offset(Integer.valueOf(facing.getFrontOffsetZ() * value)).unite(this.getZ());
      return from(x, y, z);
   }

   public WorldBounds reduce(int x, int y, int z) {
      return from(this.getX().reduce(Integer.valueOf(x)), this.getY().reduce(Integer.valueOf(y)), this.getZ().reduce(Integer.valueOf(z)));
   }

   public WorldBounds reduce(int value) {
      return this.reduce(value, value, value);
   }

   public BlockPos relative(BlockPos pos) {
      BlockPos min = this.min();
      return new BlockPos(pos.getX() - min.getX(), pos.getY() - min.getY(), pos.getZ() - min.getZ());
   }

   public boolean face(BlockPos pos) {
      return this.getX().corners(Integer.valueOf(pos.getX())) || this.getY().corners(Integer.valueOf(pos.getY())) || this.getZ().corners(Integer.valueOf(pos.getZ()));
   }

   public boolean edge(BlockPos pos) {
      int result = 0;
      if (this.getX().corners(Integer.valueOf(pos.getX()))) {
         ++result;
      }

      if (this.getY().corners(Integer.valueOf(pos.getY()))) {
         ++result;
      }

      if (this.getZ().corners(Integer.valueOf(pos.getZ()))) {
         ++result;
      }

      return result >= 2;
   }

   public boolean vertex(BlockPos pos) {
      return this.getX().corners(Integer.valueOf(pos.getX())) && this.getY().corners(Integer.valueOf(pos.getY())) && this.getZ().corners(Integer.valueOf(pos.getZ()));
   }

   public void forEach(Consumer<? super MutableBlockPos> action) {
      MutableBlockPos pos = new MutableBlockPos();
      Iterator var3 = this.getX().iterator();

      while(var3.hasNext()) {
         int x = ((Integer)var3.next()).intValue();
         Iterator var5 = this.getY().iterator();

         while(var5.hasNext()) {
            int y = ((Integer)var5.next()).intValue();
            Iterator var7 = this.getZ().iterator();

            while(var7.hasNext()) {
               int z = ((Integer)var7.next()).intValue();
               pos.setPos(x, y, z);
               action.accept(pos);
            }
         }
      }

   }

   public BlockPos min() {
      return new BlockPos(this.getX().getMin().intValue(), this.getY().getMin().intValue(), this.getZ().getMin().intValue());
   }

   public BlockPos max() {
      return new BlockPos(this.getX().getMax().intValue(), this.getY().getMax().intValue(), this.getZ().getMax().intValue());
   }

   public AxisAlignedBB toAABB() {
      return new AxisAlignedBB(this.min(), this.max());
   }
}
