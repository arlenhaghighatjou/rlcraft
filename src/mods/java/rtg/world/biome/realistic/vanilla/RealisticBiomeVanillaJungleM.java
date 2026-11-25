package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionJungle;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaJungleM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_JUNGLE;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaJungleM() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.5F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaJungleM.TerrainVanillaJungleM();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaJungleM.SurfaceVanillaJungleM(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionJungle(this.getConfig()));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.grassPerChunk = 30;
      this.baseBiome().decorator.flowersPerChunk = -999;
      this.baseBiome().decorator.treesPerChunk = 30;
   }

   public static class SurfaceVanillaJungleM extends SurfaceBase {
      public SurfaceVanillaJungleM(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  if (depth > -1 && depth < 2) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaJungleM extends TerrainBase {
      public TerrainVanillaJungleM() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainGrasslandMountains(x, y, rtgWorld, river, 4.0F, 80.0F, 68.0F);
      }
   }
}
