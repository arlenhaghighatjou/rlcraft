package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
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

public class RealisticBiomeBOPCrag extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.crag.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPCrag() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPCrag.TerrainBOPCrag(false, new float[]{2.0F, 0.5F, 6.5F, 0.5F, 14.0F, 0.5F, 19.0F, 0.5F, 23.0F, 0.5F}, 35.0F, 80.0F, 60.0F, 40.0F, 69.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPCrag.SurfaceBOPCrag(this.getConfig(), Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(), 0.0F, 1.5F, 60.0F, 65.0F, 1.8F, Blocks.GRASS.getDefaultState(), 1.9F, Blocks.GRAVEL.getDefaultState(), -0.4F);
   }

   public static class SurfaceBOPCrag extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;
      private IBlockState mix2Block;
      private float mix2Height;

      public SurfaceBOPCrag(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixHeight, IBlockState mix2, float mix2Height) {
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
         EaglercraftRandom rand = rtgWorld.rand();
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

      protected IBlockState getShadowStoneBlock() {
         return Blocks.COBBLESTONE.getDefaultState();
      }

      protected IBlockState hcCobble() {
         return Blocks.STONE.getDefaultState();
      }
   }

   public static class TerrainBOPCrag extends TerrainBase {
      private boolean booRiver;
      private float[] height;
      private int heightLength;
      private float strength;
      private float cWidth;
      private float cHeigth;
      private float cStrength;
      private float base;

      public TerrainBOPCrag(boolean riverGen, float[] heightArryay, float heightStrength, float canyonWidth, float canyonHeight, float canyonStrength, float baseHeight) {
         this.booRiver = riverGen;
         this.height = heightArryay;
         this.strength = heightStrength;
         this.heightLength = this.height.length;
         this.cWidth = canyonWidth;
         this.cHeigth = canyonHeight;
         this.cStrength = canyonStrength;
         this.base = baseHeight;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         river = river * 1.3F;
         river = river > 1.0F ? 1.0F : river;
         float r = simplex.noise2f((float)x / 100.0F, (float)y / 100.0F) * 50.0F;
         r = r < -7.4F ? -7.4F : (r > 7.4F ? 7.4F : r);
         float b = (17.0F + r) * river;
         float hn = simplex.noise2f((float)x / 12.0F, (float)y / 12.0F) * 0.5F;
         float sb = 0.0F;
         if (b > 0.0F) {
            sb = b < 0.0F ? 0.0F : (b > 7.0F ? 7.0F : b);
            sb = hn * sb;
         }

         b = b + sb;
         float cTotal = 0.0F;
         float cTemp = 0.0F;

         for(int i = 0; i < this.heightLength; i += 2) {
            cTemp = 0.0F;
            if (b > this.height[i] && border > 0.6F + this.height[i] * 0.015F + hn * 0.2F) {
               cTemp = b > this.height[i] + this.height[i + 1] ? this.height[i + 1] : b - this.height[i];
               cTemp = cTemp * this.strength;
            }

            cTotal += cTemp;
         }

         float bn = 0.0F;
         if (this.booRiver) {
            if (b < 5.0F) {
               bn = 5.0F - b;

               for(int i = 0; i < 3; ++i) {
                  bn *= bn / 4.5F;
               }
            }
         } else if (b < 5.0F) {
            bn = (simplex.noise2f((float)x / 7.0F, (float)y / 7.0F) * 1.3F + simplex.noise2f((float)x / 15.0F, (float)y / 15.0F) * 2.0F) * (5.0F - b) * 0.2F;
         }

         b = b + (cTotal - bn);
         return this.base + b;
      }
   }
}
