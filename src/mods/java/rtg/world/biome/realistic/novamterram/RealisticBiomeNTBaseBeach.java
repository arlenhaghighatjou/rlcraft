package rtg.world.biome.realistic.novamterram;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public abstract class RealisticBiomeNTBaseBeach extends RealisticBiomeNTBase {
   public RealisticBiomeNTBaseBeach(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeNTBaseBeach(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseBeach(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseBeach(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public Biome preferredBeach() {
      return this.baseBiome();
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeNTBaseBeach.TerrainVanillaBeach();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBaseBeach.SurfaceVanillaBeach(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public static class SurfaceVanillaBeach extends SurfaceBase {
      private IBlockState mixBlockFill;

      public SurfaceVanillaBeach(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), top);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (k > 63 && depth > 3 && depth < 6) {
                  primer.setBlockState(x, k, z, this.mixBlockFill);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaBeach extends TerrainBase {
      public TerrainVanillaBeach() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainBeach(x, y, rtgWorld, river, 63.0F);
      }
   }
}
