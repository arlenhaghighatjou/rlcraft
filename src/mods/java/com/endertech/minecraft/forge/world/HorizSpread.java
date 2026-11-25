package com.endertech.minecraft.forge.world;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.blocks.BlockState;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class HorizSpread {
   private final World world;
   private final BlockPos centerPos;
   private final Predicate<BlockState> canPassThrough;
   private final int radius;
   private final boolean[][] checkedMatrix;
   private final int centerIndex;
   private final Predicate<BlockState> matchedState;
   private final IntBounds indexRange;

   protected HorizSpread(World world, BlockPos centerPos, int radius, Predicate<BlockState> canSpreadThrough, Predicate<BlockState> matchedState) {
      this.world = world;
      this.centerPos = centerPos;
      this.radius = radius;
      this.canPassThrough = canSpreadThrough;
      this.matchedState = matchedState;
      int matrixSize = this.radius * 2 + 1;
      this.checkedMatrix = new boolean[matrixSize][matrixSize];
      this.centerIndex = radius;
      this.indexRange = new IntBounds(Integer.valueOf(0), matrixSize - 1);
   }

   protected boolean isInRange(BlockPos pos) {
      Vec3i matrixPos = this.getMatrixPos(pos);
      return this.indexRange.encloses(Integer.valueOf(matrixPos.getX())) && this.indexRange.encloses(Integer.valueOf(matrixPos.getY())) && this.indexRange.encloses(Integer.valueOf(matrixPos.getZ()));
   }

   protected boolean isChecked(BlockPos pos) {
      Vec3i matrixPos = this.getMatrixPos(pos);
      return this.checkedMatrix[matrixPos.getX()][matrixPos.getZ()];
   }

   protected void setChecked(BlockPos pos) {
      Vec3i matrixPos = this.getMatrixPos(pos);
      this.checkedMatrix[matrixPos.getX()][matrixPos.getZ()] = true;
   }

   protected Vec3i getMatrixPos(BlockPos pos) {
      return pos.subtract(this.centerPos).add(this.centerIndex, this.centerIndex, this.centerIndex);
   }

   @Nullable
   protected BlockPos findMatchedPos(BlockPos pos) {
      if (this.isInRange(pos) && !this.isChecked(pos)) {
         BlockState state = ForgeWorld.getBlockState(this.world, pos);
         if (this.matchedState.test(state)) {
            return pos;
         } else {
            this.setChecked(pos);
            if (this.canPassThrough.test(state)) {
               for(EnumFacing facing : ForgeWorld.Direction.getShuffledHorizontals()) {
                  BlockPos foundPos = this.findMatchedPos(pos.offset(facing));
                  if (foundPos != null) {
                     return foundPos;
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public static BlockPos findAirPos(World world, BlockPos centerPos, int radius, Predicate<BlockState> canSpreadThrough) {
      return findMatchedPos(world, centerPos, radius, canSpreadThrough, (state) -> {
         return state.isAir();
      });
   }

   @Nullable
   public static BlockPos findMatchedPos(World world, BlockPos centerPos, int radius, Predicate<BlockState> canSpreadThrough, Predicate<BlockState> matchedState) {
      BlockState state = ForgeWorld.getBlockState(world, centerPos);
      if (matchedState.test(state)) {
         return centerPos;
      } else if (canSpreadThrough.test(state)) {
         HorizSpread spread = new HorizSpread(world, centerPos, radius, canSpreadThrough, matchedState);
         return spread.findMatchedPos(centerPos);
      } else {
         return null;
      }
   }
}
