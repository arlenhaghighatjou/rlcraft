package rtg.world.biome.realistic.vanilla;

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
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionExtremeHillsCommon;
import rtg.api.world.deco.collection.DecoCollectionExtremeHillsM;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaExtremeHillsM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_EXTREME_HILLS;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaExtremeHillsM() {
      super(biome, RealisticBiomeBase.BeachType.STONE);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaExtremeHills.RidgedExtremeHills(190.0F, 67.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaExtremeHillsM.SurfaceVanillaExtremeHillsM(this.getConfig(), biome.topBlock, biome.fillerBlock, Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionExtremeHillsM(this.getConfig()));
      this.addDecoCollection(new DecoCollectionExtremeHillsCommon(this.getConfig()));
   }

   public static class SurfaceVanillaExtremeHillsM extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceVanillaExtremeHillsM(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
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

   public static class TerrainVanillaExtremeHillsM extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainVanillaExtremeHillsM(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base - 62.0F);
      }
   }
}
