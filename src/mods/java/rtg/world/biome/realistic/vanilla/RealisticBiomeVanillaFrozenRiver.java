package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaFrozenRiver extends RealisticBiomeBase {
   public static Biome biome = Biomes.FROZEN_RIVER;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaFrozenRiver() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaFrozenRiver.TerrainVanillaFrozenRiver();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaFrozenRiver.SurfaceVanillaFrozenRiver(this.getConfig());
   }

   public void initDecos() {
   }

   public static class SurfaceVanillaFrozenRiver extends SurfaceBase {
      public SurfaceVanillaFrozenRiver(BiomeConfig config) {
         super(config, Blocks.GRASS, Blocks.DIRT);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         if (river > 0.05F && river + simplex.noise2f((float)i / 10.0F, (float)j / 10.0F) * 0.15F > 0.8F) {
            for(int k = 255; k > -1; --k) {
               Block b = primer.getBlockState(x, k, z).getBlock();
               if (b == Blocks.AIR) {
                  depth = -1;
               } else if (b != Blocks.WATER) {
                  ++depth;
                  if (depth == 0 && k > 61) {
                     primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
                  } else if (depth < 4) {
                     primer.setBlockState(x, k, z, Blocks.DIRT.getDefaultState());
                  } else if (depth > 4) {
                     return;
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaFrozenRiver extends TerrainBase {
      public TerrainVanillaFrozenRiver() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainFlatLakes(x, y, rtgWorld, river, 60.0F);
      }
   }
}
