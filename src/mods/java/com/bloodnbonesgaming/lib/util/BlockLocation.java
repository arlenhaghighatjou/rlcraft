package com.bloodnbonesgaming.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLocation {
   private final BlockPos pos;

   public BlockLocation(int x, int y, int z) {
      this(new BlockPos(x, y, z));
   }

   public BlockLocation add(int x, int y, int z) {
      return new BlockLocation(this.pos.add(x, y, z));
   }

   public BlockLocation add(BlockLocation loc) {
      return this.add(loc.pos.getX(), loc.pos.getY(), loc.pos.getZ());
   }

   public BlockLocation subtract(int x, int y, int z) {
      return this.add(-x, -y, -z);
   }

   public BlockLocation subtract(BlockLocation loc) {
      return this.subtract(loc.pos.getX(), loc.pos.getY(), loc.pos.getZ());
   }

   public Block getBlock(World world) {
      return world.getBlockState(this.pos).getBlock();
   }

   public IBlockState getBlockState(World world) {
      return world.getBlockState(this.pos);
   }

   public int getMeta(World world) {
      IBlockState state = this.getBlockState(world);
      return state.getBlock().getMetaFromState(state);
   }

   public TileEntity getTileEntity(World world) {
      return world.getTileEntity(this.pos);
   }

   public BlockLocation setBlock(World world, IBlockState block) {
      world.setBlockState(this.pos, block);
      return this;
   }

   public BlockLocation rotate(double cos, double sin) {
      return new BlockLocation((int)Math.round((double)this.pos.getX() * cos + (double)this.pos.getZ() * sin), this.pos.getY(), (int)Math.round((double)(-this.pos.getX()) * sin + (double)this.pos.getZ() * cos));
   }

   public int getX() {
      return this.pos.getX();
   }

   public int getY() {
      return this.pos.getY();
   }

   public int getZ() {
      return this.pos.getZ();
   }

   public BlockLocation clone() {
      return new BlockLocation(this.pos);
   }

   public static BlockLocation fromTileEntity(TileEntity te) {
      BlockPos pos = te.getPos();
      return new BlockLocation(pos.getX(), pos.getY(), pos.getZ());
   }

   public BlockLocation(BlockPos pos) {
      this.pos = pos;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BlockLocation)) {
         return false;
      } else {
         BlockLocation other = (BlockLocation)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$pos = this.getPos();
            Object other$pos = other.getPos();
            if (this$pos == null) {
               if (other$pos != null) {
                  return false;
               }
            } else if (!this$pos.equals(other$pos)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof BlockLocation;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $pos = this.getPos();
      result = result * 59 + ($pos == null ? 43 : $pos.hashCode());
      return result;
   }

   public String toString() {
      return "BlockLocation(pos=" + this.getPos() + ")";
   }
}
