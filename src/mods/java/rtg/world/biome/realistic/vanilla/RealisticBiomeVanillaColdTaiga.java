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
import rtg.api.world.deco.collection.DecoCollectionTaiga;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaColdTaiga extends RealisticBiomeBase {
   public static Biome biome = Biomes.COLD_TAIGA;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaColdTaiga() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaColdTaiga.TerrainVanillaColdTaiga();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaColdTaiga.SurfaceVanillaColdTaiga(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionTaiga(this.getConfig(), 8.0F));
   }

   public static class SurfaceVanillaColdTaiga extends SurfaceBase {
      public SurfaceVanillaColdTaiga(BiomeConfig config, IBlockState top, IBlockState fill) {
         super(config, top, fill);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float p = simplex.noise2f((float)i / 8.0F, (float)j / 8.0F) * 0.5F;
         float c = TerrainBase.calcCliff(x, z, noise);
         int cliff = 0;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0) {
                  if (c > 0.45F && c > 1.5F - ((float)k - 60.0F) / 65.0F + p) {
                     cliff = 1;
                  }

                  if (c > 1.5F) {
                     cliff = 2;
                  }

                  if ((float)k > 110.0F + p * 4.0F && c < 0.3F + ((float)k - 100.0F) / 50.0F + p) {
                     cliff = 3;
                  }

                  if (cliff == 1) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (cliff == 3) {
                     primer.setBlockState(x, k, z, Blocks.SNOW.getDefaultState());
                  } else if (simplex.noise2f((float)i / 50.0F, (float)j / 50.0F) + p * 0.6F > 0.24F) {
                     primer.setBlockState(x, k, z, BlockUtil.getStateDirt(DirtType.PODZOL));
                  } else {
                     primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
                  }
               } else if (depth < 6) {
                  if (cliff == 1) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (cliff == 3) {
                     primer.setBlockState(x, k, z, Blocks.SNOW.getDefaultState());
                  } else {
                     primer.setBlockState(x, k, z, Blocks.DIRT.getDefaultState());
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaColdTaiga extends TerrainBase {
      public TerrainVanillaColdTaiga() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainFlatLakes(x, y, rtgWorld, river, 66.0F);
      }
   }
}
