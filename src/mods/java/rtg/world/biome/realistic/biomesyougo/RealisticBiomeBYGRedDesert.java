package rtg.world.biome.realistic.biomesyougo;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.gen.RTGChunkGenSettings;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGRedDesert extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGRedDesert(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().SURFACE_FILLER_BLOCK.set("");
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGRedDesert.TerrainVanillaDesert();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGRedDesert.SurfaceVanillaDesert(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public static class SurfaceVanillaDesert extends SurfaceBase {
      public SurfaceVanillaDesert(BiomeConfig config, IBlockState top, IBlockState fill) {
         super(config, top, fill);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         boolean water = false;
         boolean riverPaint = false;
         boolean grass = false;
         if (river > 0.05F && river + simplex.noise2f((float)i / 10.0F, (float)j / 10.0F) * 0.1F > 0.86F) {
            riverPaint = true;
            if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > 0.25F) {
               grass = true;
            }
         }

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (riverPaint) {
                  if (grass && depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  } else if (depth == 0) {
                     primer.setBlockState(x, k, z, rand.nextInt(2) == 0 ? this.topBlock : this.fillerBlock);
                  }
               } else if (depth > -1 && depth < 5) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 8) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaDesert extends TerrainBase {
      public TerrainVanillaDesert() {
         super(64.0F);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         RTGChunkGenSettings settings = rtgWorld.getGeneratorSettings();
         float duneHeight = this.minDuneHeight + (float)settings.sandDuneHeight;
         duneHeight = duneHeight * ((1.0F + rtgWorld.simplexInstance(2).noise2f((float)x / 330.0F, (float)y / 330.0F)) / 2.0F);
         float stPitch = 200.0F;
         float hPitch = 70.0F;
         float hDivisor = 40.0F;
         return terrainPolar((float)x, (float)y, rtgWorld, river, stPitch, duneHeight, hPitch, hDivisor, this.base) + groundNoise(x, y, 1.0F, rtgWorld);
      }
   }
}
