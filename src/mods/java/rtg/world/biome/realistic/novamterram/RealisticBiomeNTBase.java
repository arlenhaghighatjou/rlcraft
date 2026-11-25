package rtg.world.biome.realistic.novamterram;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.util.ModCompat;

public abstract class RealisticBiomeNTBase extends RealisticBiomeBase {
   public RealisticBiomeNTBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeNTBase(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_3_BLOCK).set("");
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBase.SurfaceNTGeneric(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, this.getMixBlock1(), 0.6F, this.getMixBlock2(), -0.2F, this.getMixBlock3(), -0.4F);
   }

   public void initDecos() {
   }

   protected IBlockState getMixBlock1() {
      return this.baseBiome().topBlock;
   }

   protected IBlockState getMixBlock2() {
      return this.baseBiome().topBlock;
   }

   protected IBlockState getMixBlock3() {
      return this.baseBiome().topBlock;
   }

   public static Biome getNTBiome(String biomeName, Biome fallbackBiome) {
      String modid = ModCompat.Mods.nt.name();
      ResourceLocation resLoc = new ResourceLocation(modid, biomeName);
      Biome biome;
      return (biome = (Biome)Biome.REGISTRY.getObject(resLoc)) != null ? biome : fallbackBiome;
   }

   public void fallenTrees(IBlockState[] fallenTreeLogs, int[] fallenTreeChances) {
      if (fallenTreeLogs.length >= 1 && fallenTreeLogs.length == fallenTreeChances.length) {
         DecoBase[] fallenTreeDecos = new DecoBase[fallenTreeLogs.length];

         for(int i = 0; i < fallenTreeLogs.length; ++i) {
            fallenTreeDecos[i] = (new DecoFallenTree()).setLogBlock(fallenTreeLogs[i]).setLogConditionChance(20).setMaxSize(4);
         }

         this.addDeco((new DecoHelperRandomSplit()).setDecos(fallenTreeDecos).setChances(fallenTreeChances));
      }
   }

   public static class SurfaceNTGeneric extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;
      private IBlockState mix2Block;
      private float mix2Height;
      private IBlockState mix3Block;
      private float mix3Height;

      public SurfaceNTGeneric(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixHeight, IBlockState mix2, float mix2Height, IBlockState mix3, float mix3Height) {
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
         this.mix3Block = this.getConfigBlock(config.SURFACE_MIX_3_BLOCK.get(), mix3);
         this.mix3Height = mix3Height;
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
                     if (mixNoise < this.mix3Height) {
                        primer.setBlockState(x, k, z, this.mix3Block);
                        m = true;
                     } else if (mixNoise < this.mix2Height) {
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
}
