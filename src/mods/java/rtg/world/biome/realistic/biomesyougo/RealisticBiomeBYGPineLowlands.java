package rtg.world.biome.realistic.biomesyougo;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGPineLowlands extends RealisticBiomeBYGBase {
   private static IBlockState bygMixBlock = BlockUtil.getBlockStateFromCfgString("byg:peatgrass", Blocks.GRASS.getDefaultState());

   public RealisticBiomeBYGPineLowlands(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public void initDecos() {
      this.fallenTrees(new IBlockState[]{BlockUtil.getBlockStateFromCfgString("byg:pinelog", BlockUtil.getStateLog(EnumType.SPRUCE)), BlockUtil.getBlockStateFromCfgString("byg:pinelog", BlockUtil.getStateLog(EnumType.SPRUCE))}, new int[]{2, 2});
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGPineLowlands.TerrainVanillaForest();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGPineLowlands.SurfaceVanillaExtremeHills(this.getConfig(), bygMixBlock, this.baseBiome().fillerBlock, Blocks.GRASS.getDefaultState(), this.baseBiome().fillerBlock, 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public static class SurfaceVanillaExtremeHills extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceVanillaExtremeHills(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.mixBlockTop = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mixTop);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), mixFill);
         this.width = mixWidth;
         this.height = mixHeight;
         this.smallW = smallWidth;
         this.smallS = smallStrength;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
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
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
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

   public static class TerrainVanillaForest extends TerrainBase {
      private float hillStrength = 12.0F;

      public TerrainVanillaForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundVariation, rtgWorld);
         float m = hills((float)x, (float)y, this.hillStrength, rtgWorld);
         float floNoise = 65.0F + this.groundNoise + m;
         return riverized(floNoise, river);
      }
   }
}
