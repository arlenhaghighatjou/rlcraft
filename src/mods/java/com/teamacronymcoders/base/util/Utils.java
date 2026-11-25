package com.teamacronymcoders.base.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Utils {
   public Utils() {
   }

   public static int getChunkXFromBlock(int blockX) {
      return blockX >> 4;
   }

   public static int getChunkXFromBlock(BlockPos position) {
      return position.getX() >> 4;
   }

   public static int getChunkZFromBlock(int blockZ) {
      return blockZ >> 4;
   }

   public static int getChunkZFromBlock(BlockPos position) {
      return position.getZ() >> 4;
   }

   public static void notifyBlockUpdate(World world, BlockPos position, IBlockState oldState, IBlockState newState) {
      if (null == oldState) {
         oldState = world.getBlockState(position);
      }

      if (null == newState) {
         newState = oldState;
      }

      world.notifyBlockUpdate(position, oldState, newState, 3);
   }

   public static long getChunkXZHashFromBlock(int blockX, int blockZ) {
      return ChunkPos.asLong(getChunkXFromBlock(blockX), getChunkZFromBlock(blockZ));
   }

   public static long getChunkXZHashFromBlock(BlockPos position) {
      return ChunkPos.asLong(getChunkXFromBlock(position), getChunkZFromBlock(position));
   }
}
