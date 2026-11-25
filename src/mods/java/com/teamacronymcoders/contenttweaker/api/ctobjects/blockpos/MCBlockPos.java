package com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IFacing;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.Locale;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class MCBlockPos implements IBlockPos {
   private BlockPos blockPos;

   public MCBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public int getX() {
      return this.blockPos.getX();
   }

   public int getY() {
      return this.blockPos.getY();
   }

   public int getZ() {
      return this.blockPos.getZ();
   }

   public crafttweaker.api.world.IBlockPos getOffset(IFacing direction, int offset) {
      return this.getOffset(direction.getName(), offset);
   }

   public Position3f asPosition3f() {
      return new MCPosition3f((float)this.getX(), (float)this.getY(), (float)this.getZ());
   }

   public IBlockPos getOffset(String directionName, int offset) {
      return new MCBlockPos(this.blockPos.offset(EnumFacing.valueOf(directionName.toUpperCase(Locale.US)), offset));
   }

   public IBlockPos getOffset(Facing facing, int offset) {
      return new MCBlockPos(this.blockPos.offset(facing.getInternal(), offset));
   }

   public BlockPos getInternal() {
      return this.blockPos;
   }
}
