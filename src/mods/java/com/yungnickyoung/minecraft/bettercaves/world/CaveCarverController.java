package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseCube;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cave.CaveCarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cave.CaveCarverBuilder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla.VanillaCaveCarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla.VanillaCaveCarverBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CaveCarverController {
   private World world;
   private VanillaCaveCarver surfaceCaveCarver;
   private FastNoise caveRegionController;
   private List<CarverNoiseRange> noiseRanges = new ArrayList();
   private boolean isDebugViewEnabled;
   private boolean isOverrideSurfaceDetectionEnabled;
   private boolean isSurfaceCavesEnabled;
   private boolean isFloodedUndergroundEnabled;

   public CaveCarverController(World worldIn, ConfigHolder config) {
      this.world = worldIn;
      this.isDebugViewEnabled = ((Boolean)config.debugVisualizer.get()).booleanValue();
      this.isOverrideSurfaceDetectionEnabled = ((Boolean)config.overrideSurfaceDetection.get()).booleanValue();
      this.isSurfaceCavesEnabled = ((Boolean)config.isSurfaceCavesEnabled.get()).booleanValue();
      this.isFloodedUndergroundEnabled = ((Boolean)config.enableFloodedUnderground.get()).booleanValue();
      this.surfaceCaveCarver = (new VanillaCaveCarverBuilder()).bottomY(((Integer)config.surfaceCaveBottom.get()).intValue()).topY(((Integer)config.surfaceCaveTop.get()).intValue()).density(((Integer)config.surfaceCaveDensity.get()).intValue()).liquidAltitude(((Integer)config.liquidAltitude.get()).intValue()).replaceGravel(((Boolean)config.replaceFloatingGravel.get()).booleanValue()).floodedUnderground(((Boolean)config.enableFloodedUnderground.get()).booleanValue()).debugVisualizerEnabled(((Boolean)config.debugVisualizer.get()).booleanValue()).debugVisualizerBlock(Blocks.EMERALD_BLOCK.getDefaultState()).build();
      float caveRegionSize = this.calcCaveRegionSize(config.caveRegionSize.get(), ((Float)config.caveRegionCustomSize.get()).floatValue());
      this.caveRegionController = new FastNoise();
      this.caveRegionController.SetSeed((int)worldIn.getSeed() + 222);
      this.caveRegionController.SetFrequency(caveRegionSize);
      this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
      this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);
      List<ICarver> carvers = new ArrayList();
      carvers.add((new CaveCarverBuilder(worldIn)).ofTypeFromConfig(CaveType.CUBIC, config).debugVisualizerBlock(Blocks.PLANKS.getDefaultState()).build());
      carvers.add((new CaveCarverBuilder(worldIn)).ofTypeFromConfig(CaveType.SIMPLEX, config).debugVisualizerBlock(Blocks.COBBLESTONE.getDefaultState()).build());
      carvers.add((new VanillaCaveCarverBuilder()).bottomY(((Integer)config.vanillaCaveBottom.get()).intValue()).topY(((Integer)config.vanillaCaveTop.get()).intValue()).density(((Integer)config.vanillaCaveDensity.get()).intValue()).priority(((Integer)config.vanillaCavePriority.get()).intValue()).liquidAltitude(((Integer)config.liquidAltitude.get()).intValue()).replaceGravel(((Boolean)config.replaceFloatingGravel.get()).booleanValue()).floodedUnderground(((Boolean)config.enableFloodedUnderground.get()).booleanValue()).debugVisualizerEnabled(((Boolean)config.debugVisualizer.get()).booleanValue()).debugVisualizerBlock(Blocks.BRICK_BLOCK.getDefaultState()).build());
      carvers.removeIf((carverx) -> {
         return carverx.getPriority() == 0;
      });
      float maxPossibleNoiseThreshold = ((Float)config.caveSpawnChance.get()).floatValue() * 0.01F * 2.0F - 1.0F;
      int totalPriority = ((Integer)carvers.stream().map(ICarver::getPriority).reduce(Integer.valueOf(0), Integer::sum)).intValue();
      float totalRangeLength = maxPossibleNoiseThreshold - -1.0F;
      float currNoise = -1.0F;
      BetterCaves.LOGGER.debug("CAVE INFORMATION");
      BetterCaves.LOGGER.debug("--> MAX POSSIBLE THRESHOLD: " + maxPossibleNoiseThreshold);
      BetterCaves.LOGGER.debug("--> TOTAL PRIORITY: " + totalPriority);
      BetterCaves.LOGGER.debug("--> TOTAL RANGE LENGTH: " + totalRangeLength);

      for(ICarver carver : carvers) {
         BetterCaves.LOGGER.debug("--> CARVER");
         float noiseRangeLength = (float)carver.getPriority() / (float)totalPriority * totalRangeLength;
         float rangeTop = currNoise + noiseRangeLength;
         CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
         currNoise = rangeTop;
         this.noiseRanges.add(range);
         BetterCaves.LOGGER.debug("    --> RANGE FOUND: " + range);
      }

   }

   public void carveChunk(ChunkPrimer primer, int chunkX, int chunkZ, int[][] surfaceAltitudes, IBlockState[][] liquidBlocks) {
      if (this.noiseRanges.size() != 0 || this.isSurfaceCavesEnabled) {
         boolean shouldCarveVanillaCaves = false;
         boolean[][] vanillaCarvingMask = new boolean[16][16];

         for(int subX = 0; subX < 4; ++subX) {
            for(int subZ = 0; subZ < 4; ++subZ) {
               int startX = subX * 4;
               int startZ = subZ * 4;
               int endX = startX + 4 - 1;
               int endZ = startZ + 4 - 1;
               BlockPos startPos = new BlockPos(chunkX * 16 + startX, 1, chunkZ * 16 + startZ);
               BlockPos endPos = new BlockPos(chunkX * 16 + endX, 1, chunkZ * 16 + endZ);
               this.noiseRanges.forEach((rangex) -> {
                  rangex.setNoiseCube((NoiseCube)null);
               });
               int maxHeight = 0;
               if (!this.isOverrideSurfaceDetectionEnabled) {
                  for(int x = startX; x < endX; ++x) {
                     for(int z = startZ; z < endZ; ++z) {
                        maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                     }
                  }

                  for(CarverNoiseRange range : this.noiseRanges) {
                     maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                  }
               }

               for(int offsetX = 0; offsetX < 4; ++offsetX) {
                  for(int offsetZ = 0; offsetZ < 4; ++offsetZ) {
                     int localX = startX + offsetX;
                     int localZ = startZ + offsetZ;
                     BlockPos colPos = new BlockPos(chunkX * 16 + localX, 1, chunkZ * 16 + localZ);
                     boolean flooded = this.isFloodedUndergroundEnabled && !this.isDebugViewEnabled && BiomeDictionary.hasType(this.world.getBiome(colPos), Type.OCEAN);
                     if (!flooded || BiomeDictionary.hasType(this.world.getBiome(colPos.east()), Type.OCEAN) && BiomeDictionary.hasType(this.world.getBiome(colPos.north()), Type.OCEAN) && BiomeDictionary.hasType(this.world.getBiome(colPos.west()), Type.OCEAN) && BiomeDictionary.hasType(this.world.getBiome(colPos.south()), Type.OCEAN)) {
                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        IBlockState liquidBlock = liquidBlocks[localX][localZ];
                        float caveRegionNoise = this.caveRegionController.GetNoise((float)colPos.getX(), (float)colPos.getZ());

                        for(CarverNoiseRange range : this.noiseRanges) {
                           if (range.contains(caveRegionNoise)) {
                              if (range.getCarver() instanceof CaveCarver) {
                                 CaveCarver carver = (CaveCarver)range.getCarver();
                                 int bottomY = carver.getBottomY();
                                 int topY = Math.min(surfaceAltitude, carver.getTopY());
                                 if (this.isOverrideSurfaceDetectionEnabled) {
                                    topY = carver.getTopY();
                                    maxHeight = carver.getTopY();
                                 }

                                 if (this.isDebugViewEnabled) {
                                    topY = 128;
                                    maxHeight = 128;
                                 }

                                 if (range.getNoiseCube() == null) {
                                    range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                                 }

                                 NoiseColumn noiseColumn = (NoiseColumn)range.getNoiseCube().get(offsetX).get(offsetZ);
                                 carver.carveColumn(primer, colPos, topY, noiseColumn, liquidBlock, flooded);
                                 break;
                              }

                              if (range.getCarver() instanceof VanillaCaveCarver) {
                                 vanillaCarvingMask[localX][localZ] = true;
                                 shouldCarveVanillaCaves = true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (shouldCarveVanillaCaves) {
            VanillaCaveCarver carver = null;

            for(CarverNoiseRange range : this.noiseRanges) {
               if (range.getCarver() instanceof VanillaCaveCarver) {
                  carver = (VanillaCaveCarver)range.getCarver();
                  break;
               }
            }

            if (carver != null) {
               carver.generate(this.world, chunkX, chunkZ, primer, true, liquidBlocks, vanillaCarvingMask);
            }
         }

         if (this.isSurfaceCavesEnabled) {
            this.surfaceCaveCarver.generate(this.world, chunkX, chunkZ, primer, false, liquidBlocks);
         }

      }
   }

   private float calcCaveRegionSize(RegionSize caveRegionSize, float caveRegionCustomSize) {
      switch(caveRegionSize) {
      case Small:
         return 0.008F;
      case Large:
         return 0.0032F;
      case ExtraLarge:
         return 0.001F;
      case Custom:
         return caveRegionCustomSize;
      default:
         return 0.005F;
      }
   }
}
