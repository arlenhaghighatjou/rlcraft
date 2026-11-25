package rtg.world.biome.realistic.traverse;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeTRAVRockyPlains extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVRockyPlains(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVRockyPlains.TerrainVanillaPlains();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeTRAVRockyPlains.SurfaceVanillaForest(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, Blocks.STONE.getDefaultState(), 0.6F, Blocks.STONE.getDefaultState(), -0.4F);
   }

   public static class SurfaceVanillaForest extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;
      private IBlockState mix2Block;
      private float mix2Height;

      public SurfaceVanillaForest(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixHeight, IBlockState mix2, float mix2Height) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.mixHeight = mixHeight;
         this.mix2Block = this.getConfigBlock(config.SURFACE_MIX_2_BLOCK.get(), mix2);
         this.mix2Height = mix2Height;
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
               if (depth == 0) {
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
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (k < 63) {
                     if (k < 62) {
                        primer.setBlockState(x, k, z, this.fillerBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else {
                     float mixNoise = simplex.noise2f((float)i / 12.0F, (float)j / 12.0F);
                     if (mixNoise < this.mix2Height) {
                        primer.setBlockState(x, k, z, this.mix2Block);
                        m = true;
                     } else if (mixNoise > this.mixHeight) {
                        primer.setBlockState(x, k, z, this.mixBlock);
                        m = true;
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  }
               } else if (depth < 6) {
                  if (cliff == 1) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaPlains extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaPlains() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
