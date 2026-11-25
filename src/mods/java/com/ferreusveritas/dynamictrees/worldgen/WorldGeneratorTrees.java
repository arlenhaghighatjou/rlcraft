package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.api.worldgen.IGroundFinder;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGeneratorTrees implements IWorldGenerator {
   private static final Map<Integer, Boolean> flatWorldsDecoration = new HashMap();
   public static List<Predicate<World>> dimensionForceGeneration = new LinkedList();

   public WorldGeneratorTrees() {
   }

   public static void clearFlatWorldCache() {
      flatWorldsDecoration.clear();
   }

   private static boolean isFlatWorldDecorated(World world) {
      FlatGeneratorInfo flatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions());
      boolean allAir = true;

      for(FlatLayerInfo flatlayerinfo : flatGeneratorInfo.getFlatLayers()) {
         IBlockState iblockstate = flatlayerinfo.getLayerMaterial();
         if (iblockstate.getBlock() != Blocks.AIR) {
            allAir = false;
         }
      }

      return (!allAir || flatGeneratorInfo.getBiome() == Biome.getIdForBiome(Biomes.VOID)) && flatGeneratorInfo.getWorldFeatures().containsKey("decoration");
   }

   public void generate(Random randomUnused, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (!dimensionForceGeneration.stream().noneMatch((p) -> {
         return p.test(world);
      }) || world.getWorldType() != WorldType.FLAT || ((Boolean)flatWorldsDecoration.computeIfAbsent(Integer.valueOf(world.provider.getDimension()), (d) -> {
         return isFlatWorldDecorated(world);
      })).booleanValue()) {
         TreeGenerator treeGenerator = TreeGenerator.getTreeGenerator();
         BiomeDataBase dbase = treeGenerator.getBiomeDataBase(world);
         if (dbase != TreeGenerator.DIMENSIONBLACKLISTED) {
            SafeChunkBounds safeBounds = new SafeChunkBounds(world, new ChunkPos(chunkX, chunkZ));
            treeGenerator.getCircleProvider().getPoissonDiscs(world, chunkX, 0, chunkZ).forEach((c) -> {
               treeGenerator.makeTree(world, dbase, c, new WorldGeneratorTrees.GroundFinder(), safeBounds);
            });
         }

      }
   }

   public static class GroundFinder implements IGroundFinder {
      public GroundFinder() {
      }

      protected boolean inNetherRange(BlockPos pos) {
         return pos.getY() >= 0 && pos.getY() <= 128;
      }

      protected boolean isReplaceable(World world, BlockPos pos) {
         return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && !world.getBlockState(pos).getMaterial().isLiquid();
      }

      protected ArrayList<Integer> findSubterraneanLayerHeights(World world, BlockPos start) {
         MutableBlockPos pos = new MutableBlockPos(new BlockPos(start.getX(), 0, start.getZ()));
         ArrayList<Integer> layers = new ArrayList();

         while(this.inNetherRange(pos)) {
            while(!this.isReplaceable(world, pos) && this.inNetherRange(pos)) {
               pos.move(EnumFacing.UP, 4);
            }

            while(this.isReplaceable(world, pos) && this.inNetherRange(pos)) {
               pos.move(EnumFacing.DOWN);
            }

            layers.add(Integer.valueOf(pos.getY()));
            pos.move(EnumFacing.UP, 16);

            while(this.isReplaceable(world, pos) && this.inNetherRange(pos)) {
               pos.move(EnumFacing.UP, 4);
            }
         }

         if (layers.size() > 0) {
            layers.remove(layers.size() - 1);
         }

         return layers;
      }

      protected BlockPos findSubterraneanGround(World world, BlockPos start) {
         ArrayList<Integer> layers = this.findSubterraneanLayerHeights(world, start);
         if (layers.size() < 1) {
            return BlockPos.ORIGIN;
         } else {
            int y = ((Integer)layers.get(world.rand.nextInt(layers.size()))).intValue();
            return new BlockPos(start.getX(), y, start.getZ());
         }
      }

      protected boolean inOverworldRange(BlockPos pos) {
         return pos.getY() >= 0 && pos.getY() <= 255;
      }

      protected BlockPos findOverworldGround(World world, BlockPos start) {
         Chunk chunk = world.getChunkFromBlockCoords(start);

         for(MutableBlockPos mPos = (new MutableBlockPos(world.getHeight(start))).move(EnumFacing.UP, 2); this.inOverworldRange(mPos); mPos.move(EnumFacing.DOWN)) {
            IBlockState state = chunk.getBlockState(mPos);
            Block testBlock = state.getBlock();
            if (testBlock != Blocks.AIR) {
               Material material = state.getMaterial();
               if (material == Material.GROUND || material == Material.WATER || state.getMaterial().blocksMovement() && !testBlock.isLeaves(state, world, mPos) && !testBlock.isFoliage(world, mPos)) {
                  return mPos.toImmutable();
               }
            }
         }

         return BlockPos.ORIGIN;
      }

      public BlockPos findGround(BiomeDataBase.BiomeEntry biomeEntry, World world, BlockPos start) {
         return biomeEntry.isSubterraneanBiome() ? this.findSubterraneanGround(world, start) : this.findOverworldGround(world, start);
      }
   }
}
