package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.deco.collection.DecoCollectionSavanna;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaSavannaM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_SAVANNA;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaSavannaM() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaSavannaM.TerrainVanillaSavannaM(true, 35.0F, 160.0F, 60.0F, 40.0F, 69.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaSavannaM.SurfaceVanillaSavannaM(this.getConfig(), Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, BlockUtil.getStateDirt(DirtType.COARSE_DIRT));
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      this.addDecoCollection(new DecoCollectionSavanna(this.getConfig()));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public static class SurfaceVanillaSavannaM extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;

      public SurfaceVanillaSavannaM(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         int cliff = 0;
         boolean m = false;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               float p = simplex.noise3f((float)i / 8.0F, (float)j / 8.0F, (float)k / 8.0F) * 0.5F;
               if (c > this.min && c > this.sCliff - ((float)k - this.sHeight) / this.sStrength + p) {
                  cliff = 1;
               }

               if (c > this.cCliff) {
                  cliff = 2;
               }

               if (cliff == 1) {
                  if (rand.nextInt(3) == 0) {
                     primer.setBlockState(x, k, z, this.hcCobble());
                  } else {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (cliff == 2) {
                  if (depth > -1 && depth < 2) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (k > 74) {
                  if (depth == 0) {
                     if (rand.nextInt(5) == 0) {
                        primer.setBlockState(x, k, z, this.mixBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               } else if (depth == 0 && k > 61) {
                  int r = (int)((float)(k - 62) / 2.0F);
                  if (rand.nextInt(r + 2) == 0) {
                     primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
                  } else if (rand.nextInt((int)((float)r / 2.0F) + 2) == 0) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaSavannaM extends TerrainBase {
      private float[] height = new float[]{18.0F, 0.4F, 12.0F, 0.6F, 8.0F, 0.8F, 4.0F, 0.8F};
      private int heightLength;
      private float strength = 10.0F;

      public TerrainVanillaSavannaM(boolean riverGen, float heightStrength, float canyonWidth, float canyonHeight, float canyonStrength, float baseHeight) {
         this.heightLength = this.height.length;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlateau((float)x, (float)y, rtgWorld, river, this.height, border, this.strength, this.heightLength, 50.0F, true);
      }
   }
}
