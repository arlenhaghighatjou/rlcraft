package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class CarverUtils {
   private static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private static final IBlockState SAND = Blocks.SAND.getDefaultState();
   private static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
   private static final IBlockState REDSANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
   private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final IBlockState ANDESITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
   private static final ImmutableSet<IBlockState> DEBUG_BLOCKS = ImmutableSet.of(Blocks.GOLD_BLOCK.getDefaultState(), Blocks.PLANKS.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.REDSTONE_BLOCK.getDefaultState(), Blocks.EMERALD_BLOCK.getDefaultState(), Blocks.BRICK_BLOCK.getDefaultState(), new IBlockState[0]);

   private CarverUtils() {
   }

   public static void digBlock(World world, ChunkPrimer primer, BlockPos blockPos, IBlockState airBlockState, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
      int localX = BetterCavesUtils.getLocal(blockPos.getX());
      int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
      int y = blockPos.getY();
      IBlockState blockState = primer.getBlockState(localX, y, localZ);
      IBlockState blockStateAbove = primer.getBlockState(localX, y + 1, localZ);
      Biome biome = world.getBiome(blockPos);
      Block biomeTopBlock = biome.topBlock.getBlock();
      Block biomeFillerBlock = biome.fillerBlock.getBlock();
      if (canReplaceBlock(blockState, blockStateAbove) || blockState.getBlock() == biomeTopBlock || blockState.getBlock() == biomeFillerBlock) {
         if (airBlockState == AIR && y <= liquidAltitude) {
            if (liquidBlockState != null) {
               primer.setBlockState(localX, y, localZ, liquidBlockState);
            }
         } else {
            if (airBlockState == AIR && isWaterAdjacent(primer, blockPos)) {
               return;
            }

            if (isTopBlock(world, primer, blockPos) && canReplaceBlock(primer.getBlockState(localX, y - 1, localZ), AIR)) {
               primer.setBlockState(localX, y - 1, localZ, biome.topBlock);
            }

            if (blockStateAbove == SAND) {
               primer.setBlockState(localX, y + 1, localZ, SANDSTONE);
            } else if (blockStateAbove == SAND.withProperty(BlockSand.VARIANT, net.minecraft.block.BlockSand.EnumType.RED_SAND)) {
               primer.setBlockState(localX, y + 1, localZ, REDSANDSTONE);
            }

            if (replaceGravel && blockStateAbove == GRAVEL) {
               primer.setBlockState(localX, y + 1, localZ, ANDESITE);
            }

            primer.setBlockState(localX, y, localZ, airBlockState);
         }
      }

   }

   public static void digBlock(World world, ChunkPrimer primer, BlockPos blockPos, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
      digBlock(world, primer, blockPos, Blocks.AIR.getDefaultState(), liquidBlockState, liquidAltitude, replaceGravel);
   }

   public static void digBlock(World world, ChunkPrimer primer, int x, int y, int z, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
      digBlock(world, primer, new BlockPos(x, y, z), liquidBlockState, liquidAltitude, replaceGravel);
   }

   public static void digBlock(World world, ChunkPrimer primer, int x, int y, int z, IBlockState airBlockState, IBlockState liquidBlockState, int liquidAltitude, boolean replaceGravel) {
      digBlock(world, primer, new BlockPos(x, y, z), airBlockState, liquidBlockState, liquidAltitude, replaceGravel);
   }

   public static void debugDigBlock(ChunkPrimer primer, BlockPos blockPos, IBlockState blockState, boolean digBlock) {
      int localX = BetterCavesUtils.getLocal(blockPos.getX());
      int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
      int y = blockPos.getY();
      if (!DEBUG_BLOCKS.contains(primer.getBlockState(localX, y, localZ))) {
         if (digBlock) {
            primer.setBlockState(localX, y, localZ, blockState);
         } else {
            primer.setBlockState(localX, y, localZ, Blocks.AIR.getDefaultState());
         }

      }
   }

   public static void debugDigBlock(ChunkPrimer primer, int x, int y, int z, IBlockState blockState, boolean digBlock) {
      debugDigBlock(primer, new BlockPos(x, y, z), blockState, digBlock);
   }

   public static boolean isTopBlock(World world, ChunkPrimer primer, BlockPos blockPos) {
      int localX = BetterCavesUtils.getLocal(blockPos.getX());
      int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
      int y = blockPos.getY();
      Biome biome = world.getBiome(blockPos);
      IBlockState blockState = primer.getBlockState(localX, y, localZ);
      return blockState == biome.topBlock;
   }

   public static boolean canReplaceBlock(IBlockState blockState, IBlockState blockStateAbove) {
      Block block = blockState.getBlock();
      if (block != Blocks.LEAVES && block != Blocks.LEAVES2 && block != Blocks.LOG && block != Blocks.LOG2) {
         if (blockStateAbove != Blocks.LOG.getDefaultState() && blockStateAbove != Blocks.LOG2.getDefaultState()) {
            if (blockState == Blocks.BEDROCK.getDefaultState()) {
               return false;
            } else if (blockState.getMaterial() == Material.ROCK) {
               return true;
            } else if (block != Blocks.STONE && block != Blocks.DIRT && block != Blocks.GRASS && block != Blocks.HARDENED_CLAY && block != Blocks.STAINED_HARDENED_CLAY && block != Blocks.SANDSTONE && block != Blocks.RED_SANDSTONE && block != Blocks.MYCELIUM && block != Blocks.SNOW_LAYER) {
               return (block == Blocks.SAND || block == Blocks.GRAVEL) && blockStateAbove.getMaterial() != Material.WATER;
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isWaterAdjacent(ChunkPrimer primer, BlockPos blockPos) {
      int localX = BetterCavesUtils.getLocal(blockPos.getX());
      int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
      int y = blockPos.getY();
      return primer.getBlockState(localX, y + 1, localZ).getMaterial() == Material.WATER || localX < 15 && primer.getBlockState(localX + 1, y, localZ).getMaterial() == Material.WATER || localX > 0 && primer.getBlockState(localX - 1, y, localZ).getMaterial() == Material.WATER || localZ < 15 && primer.getBlockState(localX, y, localZ + 1).getMaterial() == Material.WATER || localZ > 0 && primer.getBlockState(localX, y, localZ - 1).getMaterial() == Material.WATER;
   }
}
