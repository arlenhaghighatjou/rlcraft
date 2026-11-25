package rtg.world.biome;

import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rtg.api.RTGAPI;
import rtg.api.util.CircularSearchCreator;
import rtg.api.world.biome.IRealisticBiome;
import rtg.world.gen.ChunkLandscape;

public final class BiomeAnalyzer {
   private static final int NO_BIOME = -1;
   private static final int MAX_BIOMES = 256;
   private boolean[] riverBiome = new boolean[256];
   private boolean[] oceanBiome = new boolean[256];
   private boolean[] swampBiome = new boolean[256];
   private boolean[] beachBiome = new boolean[256];
   private boolean[] landBiome = new boolean[256];
   private int[] preferredBeach;
   private IRealisticBiome scenicLakeBiome = RTGAPI.getRTGBiome(Biomes.RIVER);
   private IRealisticBiome scenicFrozenLakeBiome = RTGAPI.getRTGBiome(Biomes.FROZEN_RIVER);
   private BiomeAnalyzer.SmoothingSearchStatus beachSearch;
   private BiomeAnalyzer.SmoothingSearchStatus landSearch;
   private BiomeAnalyzer.SmoothingSearchStatus oceanSearch;

   public BiomeAnalyzer() {
      this.initBiomes();
      this.setupBeachesForBiomes();
      this.setSearches();
   }

   public int[] xyinverted() {
      int[] result = new int[256];

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            result[i * 16 + j] = j * 16 + i;
         }
      }

      for(int i = 0; i < 256; ++i) {
         if (result[result[i]] != i) {
            throw new RuntimeException("" + i + " " + result[i] + " " + result[result[i]]);
         }
      }

      return result;
   }

   private void initBiomes() {
      ForgeRegistries.BIOMES.getValuesCollection().forEach((biome) -> {
         int id = Biome.getIdForBiome(biome);
         if (BiomeDictionary.hasType(biome, Type.OCEAN)) {
            this.oceanBiome[id] = true;
         } else if (BiomeDictionary.hasType(biome, Type.RIVER)) {
            this.riverBiome[id] = true;
         } else if (BiomeDictionary.hasType(biome, Type.SWAMP)) {
            this.swampBiome[id] = true;
         } else if (BiomeDictionary.hasType(biome, Type.BEACH)) {
            this.beachBiome[id] = true;
         } else {
            this.landBiome[id] = true;
         }

      });
   }

   private void setupBeachesForBiomes() {
      this.preferredBeach = new int[256];

      for(int i = 0; i < this.preferredBeach.length; ++i) {
         Biome biome = Biome.getBiome(i);
         if (biome != null) {
            IRealisticBiome realisticBiome = RTGAPI.RTG_BIOMES.getValueAt(Integer.valueOf(i));
            if (realisticBiome != null) {
               this.preferredBeach[i] = realisticBiome.getBeachBiome().baseBiomeId();
            }
         }
      }

   }

   public void newRepair(Biome[] genLayerBiomes, int[] biomeNeighborhood, ChunkLandscape landscape) {
      IRealisticBiome[] jitteredBiomes = landscape.biome;
      float[] noise = landscape.noise;
      float[] riverStrength = landscape.river;

      for(int i = 0; i < 256; ++i) {
         IRealisticBiome realisticBiome = RTGAPI.getRTGBiome(genLayerBiomes[i]);
         int realisticBiomeId = realisticBiome.baseBiomeId();
         boolean canBeRiver = (double)riverStrength[i] > 0.7D;
         if ((double)noise[i] > 61.5D) {
            jitteredBiomes[i] = realisticBiome;
         } else if (canBeRiver && !this.oceanBiome[realisticBiomeId] && !this.swampBiome[realisticBiomeId]) {
            jitteredBiomes[i] = realisticBiome.getRiverBiome();
         } else {
            jitteredBiomes[i] = realisticBiome;
         }
      }

      this.beachSearch.setNotHunted();
      this.beachSearch.setAbsent();
      float beachTop = 64.5F;

      for(int i = 0; i < 256 && !this.beachSearch.isAbsent(); ++i) {
         float beachBottom = 61.5F;
         if (noise[i] >= beachBottom && noise[i] <= this.riverAdjusted(beachTop, riverStrength[i])) {
            int biomeID = Biome.getIdForBiome(jitteredBiomes[i].baseBiome());
            if (!this.swampBiome[biomeID]) {
               if (this.beachSearch.isNotHunted()) {
                  this.beachSearch.hunt(biomeNeighborhood);
                  this.landSearch.hunt(biomeNeighborhood);
               }

               int foundBiome = this.beachSearch.biomes[i];
               if (foundBiome != -1) {
                  int nearestLandBiome = this.landSearch.biomes[i];
                  if (nearestLandBiome > -1) {
                     foundBiome = this.preferredBeach[nearestLandBiome];
                  }

                  jitteredBiomes[i] = RTGAPI.getRTGBiome(foundBiome);
               }
            }
         }
      }

      this.landSearch.setAbsent();
      this.landSearch.setNotHunted();

      for(int i = 0; i < 256 && (!this.landSearch.isAbsent() || !this.beachSearch.isAbsent()); ++i) {
         if (noise[i] >= this.riverAdjusted(beachTop, riverStrength[i])) {
            int biomeID = Biome.getIdForBiome(jitteredBiomes[i].baseBiome());
            if (!this.landBiome[biomeID] && !this.swampBiome[biomeID]) {
               if (this.landSearch.isNotHunted()) {
                  this.landSearch.hunt(biomeNeighborhood);
               }

               int foundBiome = this.landSearch.biomes[i];
               if (foundBiome == -1) {
                  if (this.beachSearch.isNotHunted()) {
                     this.beachSearch.hunt(biomeNeighborhood);
                  }

                  foundBiome = this.beachSearch.biomes[i];
               }

               if (foundBiome != -1) {
                  jitteredBiomes[i] = RTGAPI.getRTGBiome(foundBiome);
               }
            }
         }
      }

      this.oceanSearch.setAbsent();
      this.oceanSearch.setNotHunted();

      for(int i = 0; i < 256 && !this.oceanSearch.isAbsent(); ++i) {
         float oceanTop = 61.5F;
         if (noise[i] <= oceanTop) {
            int biomeID = Biome.getIdForBiome(jitteredBiomes[i].baseBiome());
            if (!this.oceanBiome[biomeID] && !this.swampBiome[biomeID] && !this.riverBiome[biomeID]) {
               if (this.oceanSearch.isNotHunted()) {
                  this.oceanSearch.hunt(biomeNeighborhood);
               }

               int foundBiome = this.oceanSearch.biomes[i];
               if (foundBiome != -1) {
                  jitteredBiomes[i] = RTGAPI.getRTGBiome(foundBiome);
               }
            }
         }
      }

      for(int i = 0; i < 256; ++i) {
         int biomeID = Biome.getIdForBiome(jitteredBiomes[i].baseBiome());
         if ((double)noise[i] <= 61.5D && !this.riverBiome[biomeID] && !this.oceanBiome[biomeID] && !this.swampBiome[biomeID] && !this.beachBiome[biomeID]) {
            int riverReplacement = jitteredBiomes[i].getRiverBiome().baseBiomeId();
            if (riverReplacement == Biome.getIdForBiome(Biomes.FROZEN_RIVER)) {
               jitteredBiomes[i] = this.scenicFrozenLakeBiome;
            } else {
               jitteredBiomes[i] = this.scenicLakeBiome;
            }
         }
      }

   }

   private void setSearches() {
      this.beachSearch = new BiomeAnalyzer.SmoothingSearchStatus(this.beachBiome);
      this.landSearch = new BiomeAnalyzer.SmoothingSearchStatus(this.landBiome);
      this.oceanSearch = new BiomeAnalyzer.SmoothingSearchStatus(this.oceanBiome);
   }

   private float riverAdjusted(float top, float river) {
      if (river >= 1.0F) {
         return top;
      } else {
         float erodedRiver = river / 0.09375F;
         if (erodedRiver <= 1.0F) {
            top = top * (1.0F - erodedRiver) + 62.0F * erodedRiver;
         }

         top = top * (1.0F - river) + 62.0F * river;
         return top;
      }
   }

   private static final class SmoothingSearchStatus {
      private final int upperLeftFinding;
      private final int upperRightFinding;
      private final int lowerLeftFinding;
      private final int lowerRightFinding;
      private final int[] quadrantBiome;
      private final float[] quadrantBiomeWeighting;
      public int[] biomes;
      private boolean absent;
      private boolean notHunted;
      private int[] findings;
      private float[] weightings;
      private boolean[] desired;
      private int arraySize;
      private int[] pattern;
      private int biomeCount;

      private SmoothingSearchStatus(boolean[] desired) {
         this.upperLeftFinding = 0;
         this.upperRightFinding = 3;
         this.lowerLeftFinding = 1;
         this.lowerRightFinding = 4;
         this.quadrantBiome = new int[4];
         this.quadrantBiomeWeighting = new float[4];
         this.biomes = new int[256];
         this.absent = false;
         this.findings = new int[9];
         this.weightings = new float[9];
         this.desired = desired;
      }

      private int size() {
         return 3;
      }

      private void hunt(int[] biomeNeighborhood) {
         this.clear();
         int oldArraySize = this.arraySize;
         this.arraySize = (int)Math.sqrt((double)biomeNeighborhood.length);
         if (this.arraySize * this.arraySize != biomeNeighborhood.length) {
            throw new RuntimeException("non-square array");
         } else {
            if (this.arraySize != oldArraySize) {
               this.pattern = (new CircularSearchCreator()).pattern((float)this.arraySize / 2.0F - 1.0F, this.arraySize);
            }

            for(int xOffset = -1; xOffset <= 1; ++xOffset) {
               for(int zOffset = -1; zOffset <= 1; ++zOffset) {
                  this.search(xOffset, zOffset, biomeNeighborhood);
               }
            }

            this.smoothBiomes();
         }
      }

      private void search(int xOffset, int zOffset, int[] biomeNeighborhood) {
         int offset = xOffset * this.arraySize + zOffset;
         int location = (xOffset + 1) * this.size() + zOffset + 1;
         this.findings[location] = -1;
         this.weightings[location] = 2.0F;

         for(int i = 0; i < this.pattern.length; ++i) {
            int biome = biomeNeighborhood[this.pattern[i] + offset];
            if (this.desired[biome]) {
               this.findings[location] = biome;
               this.weightings[location] = (float)Math.sqrt((double)this.pattern.length) - (float)Math.sqrt((double)i) + 2.0F;
               break;
            }
         }

      }

      private void smoothBiomes() {
         this.smoothQuadrant(this.biomeIndex(0, 0), 0);
         this.smoothQuadrant(this.biomeIndex(8, 0), 3);
         this.smoothQuadrant(this.biomeIndex(0, 8), 1);
         this.smoothQuadrant(this.biomeIndex(8, 8), 4);
      }

      private void smoothQuadrant(int biomesOffset, int findingsOffset) {
         int upperLeft = this.findings[0 + findingsOffset];
         int upperRight = this.findings[3 + findingsOffset];
         int lowerLeft = this.findings[1 + findingsOffset];
         int lowerRight = this.findings[4 + findingsOffset];
         if (upperLeft == upperRight && upperLeft == lowerLeft && upperLeft == lowerRight) {
            for(int x = 0; x < 8; ++x) {
               for(int z = 0; z < 8; ++z) {
                  this.biomes[this.biomeIndex(x, z) + biomesOffset] = upperLeft;
               }
            }

         } else {
            this.biomeCount = 0;
            this.addBiome(upperLeft);
            this.addBiome(upperRight);
            this.addBiome(lowerLeft);
            this.addBiome(lowerRight);

            for(int x = 0; x < 8; ++x) {
               for(int z = 0; z < 8; ++z) {
                  this.addBiome(lowerRight);

                  for(int i = 0; i < 4; ++i) {
                     this.quadrantBiomeWeighting[i] = 0.0F;
                  }

                  this.addWeight(upperLeft, this.weightings[0 + findingsOffset] * (float)(7 - x) * (float)(7 - z));
                  this.addWeight(upperRight, this.weightings[3 + findingsOffset] * (float)x * (float)(7 - z));
                  this.addWeight(lowerLeft, this.weightings[1 + findingsOffset] * (float)(7 - x) * (float)z);
                  this.addWeight(lowerRight, this.weightings[4 + findingsOffset] * (float)x * (float)z);
                  this.biomes[this.biomeIndex(x, z) + biomesOffset] = this.preferredBiome();
               }
            }

         }
      }

      private void addBiome(int biome) {
         for(int i = 0; i < this.biomeCount; ++i) {
            if (biome == this.quadrantBiome[i]) {
               return;
            }
         }

         this.quadrantBiome[this.biomeCount++] = biome;
      }

      private void addWeight(int biome, float weight) {
         for(int i = 0; i < this.biomeCount; ++i) {
            if (biome == this.quadrantBiome[i]) {
               this.quadrantBiomeWeighting[i] += weight;
               return;
            }
         }

      }

      private int preferredBiome() {
         float bestWeight = 0.0F;
         int result = -2;

         for(int i = 0; i < this.biomeCount; ++i) {
            if (this.quadrantBiomeWeighting[i] > bestWeight) {
               bestWeight = this.quadrantBiomeWeighting[i];
               result = this.quadrantBiome[i];
            }
         }

         return result;
      }

      private int biomeIndex(int x, int z) {
         return x * 16 + z;
      }

      private void clear() {
         Arrays.fill(this.findings, -1);
      }

      private boolean isAbsent() {
         return this.absent;
      }

      private void setAbsent() {
         this.absent = false;
      }

      private boolean isNotHunted() {
         return this.notHunted;
      }

      private void setNotHunted() {
         this.notHunted = true;
      }
   }
}
