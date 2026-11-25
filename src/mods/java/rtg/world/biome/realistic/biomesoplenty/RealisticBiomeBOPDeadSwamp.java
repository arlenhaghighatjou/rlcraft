package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.JitterEffect;

public class RealisticBiomeBOPDeadSwamp extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.dead_swamp.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPDeadSwamp() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPDeadSwamp.TerrainBOPDeadSwamp();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPDeadSwamp.SurfaceBOPDeadSwamp(this.getConfig(), BOPBlocks.grass.getDefaultState(), BOPBlocks.dirt.getDefaultState(), BOPBlocks.mud.getDefaultState());
   }

   public static class SurfaceBOPDeadSwamp extends SurfaceBase {
      private IBlockState mix;
      private float mixHeight;

      public SurfaceBOPDeadSwamp(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixBlock) {
         super(config, top, filler);
         this.mix = mixBlock;
         this.mixHeight = 0.1F;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(2);
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff && k > 64) {
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
                  if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > this.mixHeight + (noise[x * 16 + z] - 63.0F) / 10.0F) {
                     primer.setBlockState(x, k, z, this.mix);
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

   public static class TerrainBOPDeadSwamp extends TerrainBase {
      private HeightEffect height;

      public TerrainBOPDeadSwamp() {
         HeightVariation waterLand = new HeightVariation();
         waterLand.height = 2.0F;
         waterLand.wavelength = 40.0F;
         waterLand.octave = 0;
         this.height = new JitterEffect(5.0F, 10.0F, waterLand);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return 62.0F + this.height.added(rtgWorld, (float)x, (float)y);
      }
   }
}
