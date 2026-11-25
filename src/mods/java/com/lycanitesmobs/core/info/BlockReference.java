package com.lycanitesmobs.core.info;

import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReference {
   protected final World world;
   protected final BlockPos pos;

   public BlockReference(World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
   }

   public World getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public IBlockState getState() {
      return this.getWorld().getBlockState(this.getPos());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof BlockReference)) {
         return false;
      } else {
         BlockReference that = (BlockReference)o;
         return this.world.equals(that.world) && this.pos.equals(that.pos);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.world, this.pos});
   }
}
