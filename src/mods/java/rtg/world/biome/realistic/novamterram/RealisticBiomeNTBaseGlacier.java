package rtg.world.biome.realistic.novamterram;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public abstract class RealisticBiomeNTBaseGlacier extends RealisticBiomeNTBase {
   public RealisticBiomeNTBaseGlacier(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeNTBaseGlacier(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseGlacier(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseGlacier(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeNTBaseGlacier.TerrainBOPGlacier(230.0F, 40.0F, 68.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBaseGlacier.SurfaceBOPGlacier(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, this.baseBiome().topBlock, this.baseBiome().fillerBlock, Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public static class SurfaceBOPGlacier extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private IBlockState cliffBlock1;
      private IBlockState cliffBlock2;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceBOPGlacier(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, IBlockState cliff1, IBlockState cliff2, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.mixBlockTop = mixTop;
         this.mixBlockFill = mixFill;
         this.cliffBlock1 = cliff1;
         this.cliffBlock2 = cliff2;
         this.width = mixWidth;
         this.height = mixHeight;
         this.smallW = smallWidth;
         this.smallS = smallStrength;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;
         boolean mix = false;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  if (depth > -1 && depth < 2) {
                     primer.setBlockState(x, k, z, rand.nextInt(3) == 0 ? this.cliffBlock2 : this.cliffBlock1);
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.cliffBlock1);
                  }
               } else if (depth == 0 && k > 61) {
                  if (simplex.noise2f((float)i / this.width, (float)j / this.width) + simplex.noise2f((float)i / this.smallW, (float)j / this.smallW) * this.smallS > this.height) {
                     primer.setBlockState(x, k, z, this.mixBlockTop);
                     mix = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  if (mix) {
                     primer.setBlockState(x, k, z, this.mixBlockFill);
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainBOPGlacier extends TerrainBase {
      private float width;
      private float strength;

      public TerrainBOPGlacier(float mountainWidth, float mountainStrength, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.base = height;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainLonelyMountain(x, y, rtgWorld, river, this.strength, this.width, this.base);
      }
   }
}
