package com.yungnickyoung.minecraft.bettercaves.world.carver.cave;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class CaveCarver implements ICarver {
   private CarverSettings settings;
   private NoiseGen noiseGen;
   private World world;
   private int surfaceCutoff;
   private int bottomY;
   private int topY;
   private boolean enableYAdjust;
   private float yAdjustF1;
   private float yAdjustF2;

   public CaveCarver(CaveCarverBuilder builder) {
      this.settings = builder.getSettings();
      this.noiseGen = new NoiseGen(this.settings.getWorld(), this.settings.isFastNoise(), this.settings.getNoiseSettings(), this.settings.getNumGens(), this.settings.getyCompression(), this.settings.getXzCompression());
      this.world = builder.getSettings().getWorld();
      this.surfaceCutoff = builder.getSurfaceCutoff();
      this.bottomY = builder.getBottomY();
      this.topY = builder.getTopY();
      this.enableYAdjust = builder.isEnableYAdjust();
      this.yAdjustF1 = builder.getyAdjustF1();
      this.yAdjustF2 = builder.getyAdjustF2();
      if (this.bottomY > this.topY) {
         BetterCaves.LOGGER.warn("Warning: Min altitude for caves should not be greater than max altitude.");
         BetterCaves.LOGGER.warn("Using default values...");
         this.bottomY = 1;
         this.topY = 80;
      }

   }

   public void carveColumn(ChunkPrimer primer, BlockPos colPos, int topY, NoiseColumn noises, IBlockState liquidBlock, boolean flooded) {
      int localX = BetterCavesUtils.getLocal(colPos.getX());
      int localZ = BetterCavesUtils.getLocal(colPos.getZ());
      if (localX >= 0 && localX <= 15) {
         if (localZ >= 0 && localZ <= 15) {
            if (this.bottomY >= 0 && this.bottomY <= 255) {
               if (topY >= 0 && topY <= 255) {
                  int transitionBoundary = topY - this.surfaceCutoff;
                  if (transitionBoundary < 1) {
                     transitionBoundary = 1;
                  }

                  Map<Integer, Float> thresholds = this.generateThresholds(topY, this.bottomY, transitionBoundary);
                  if (this.enableYAdjust) {
                     this.preprocessCaveNoiseCol(noises, topY, this.bottomY, thresholds, this.settings.getNumGens());
                  }

                  for(int y = topY; y >= this.bottomY && (y > this.settings.getLiquidAltitude() || liquidBlock != null); --y) {
                     List<Double> noiseBlock = noises.get(y).getNoiseValues();
                     boolean digBlock = true;
                     Iterator iter = noiseBlock.iterator();

                     while(iter.hasNext()) {
                        double noise = ((Double)iter.next()).doubleValue();
                        if (noise < (double)((Float)thresholds.get(Integer.valueOf(y))).floatValue()) {
                           digBlock = false;
                           break;
                        }
                     }

                     IBlockState airBlockState = flooded && y < this.world.getSeaLevel() ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                     BlockPos blockPos = new BlockPos(localX, y, localZ);
                     if (this.settings.isEnableDebugVisualizer()) {
                        CarverUtils.debugDigBlock(primer, blockPos, this.settings.getDebugBlock(), digBlock);
                     } else if (digBlock) {
                        CarverUtils.digBlock(this.settings.getWorld(), primer, blockPos, airBlockState, liquidBlock, this.settings.getLiquidAltitude(), this.settings.isReplaceFloatingGravel());
                     }
                  }

               }
            }
         }
      }
   }

   private void preprocessCaveNoiseCol(NoiseColumn noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
      for(int realY = topY; realY >= bottomY; --realY) {
         NoiseTuple noiseBlock = noises.get(realY);
         float threshold = ((Float)thresholds.get(Integer.valueOf(realY))).floatValue();
         boolean valid = true;
         Iterator iter = noiseBlock.getNoiseValues().iterator();

         while(iter.hasNext()) {
            double noise = ((Double)iter.next()).doubleValue();
            if (noise < (double)threshold) {
               valid = false;
               break;
            }
         }

         if (valid) {
            float f1 = this.yAdjustF1;
            float f2 = this.yAdjustF2;
            if (realY < topY) {
               NoiseTuple tupleAbove = noises.get(realY + 1);

               for(int i = 0; i < numGens; ++i) {
                  tupleAbove.set(i, (double)(1.0F - f1) * tupleAbove.get(i) + (double)f1 * noiseBlock.get(i));
               }
            }

            if (realY < topY - 1) {
               NoiseTuple tupleTwoAbove = noises.get(realY + 2);

               for(int i = 0; i < numGens; ++i) {
                  tupleTwoAbove.set(i, (double)(1.0F - f2) * tupleTwoAbove.get(i) + (double)f2 * noiseBlock.get(i));
               }
            }
         }
      }

   }

   private Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
      Map<Integer, Float> thresholds = new HashMap();

      for(int realY = bottomY; realY <= topY; ++realY) {
         float noiseThreshold = this.settings.getNoiseThreshold();
         if (realY >= transitionBoundary) {
            noiseThreshold *= 1.0F + 0.3F * ((float)(realY - transitionBoundary) / (float)(topY - transitionBoundary));
         }

         thresholds.put(Integer.valueOf(realY), Float.valueOf(noiseThreshold));
      }

      return thresholds;
   }

   public NoiseGen getNoiseGen() {
      return this.noiseGen;
   }

   public CarverSettings getSettings() {
      return this.settings;
   }

   public int getPriority() {
      return this.settings.getPriority();
   }

   public int getBottomY() {
      return this.bottomY;
   }

   public int getTopY() {
      return this.topY;
   }
}
