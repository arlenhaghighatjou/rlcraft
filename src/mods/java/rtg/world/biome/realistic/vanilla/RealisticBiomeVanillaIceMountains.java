package rtg.world.biome.realistic.vanilla;

import java.util.Random;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaIceMountains extends RealisticBiomeBase {
   public static Biome biome = Biomes.ICE_MOUNTAINS;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaIceMountains() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().addProperty(this.getConfig().USE_ARCTIC_SURFACE).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaIceMountains.TerrainVanillaIceMountains(230.0F, 60.0F, 68.0F);
   }

   public SurfaceBase initSurface() {
      return this.getConfig().USE_ARCTIC_SURFACE.get() ? new RealisticBiomeVanillaIceMountains.SurfaceVanillaIceMountains(this.getConfig(), Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState(), Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F) : new RealisticBiomeVanillaIceMountains.SurfaceVanillaIceMountains(this.getConfig(), biome.topBlock, biome.fillerBlock, Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState(), Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public void initDecos() {
   }

   public static class SurfaceVanillaIceMountains extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private IBlockState cliffBlock1;
      private IBlockState cliffBlock2;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceVanillaIceMountains(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, IBlockState cliff1, IBlockState cliff2, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.mixBlockTop = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mixTop);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), mixFill);
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

   public static class TerrainVanillaIceMountains extends TerrainBase {
      private float width;
      private float strength;
      private float terrainHeight;

      public TerrainVanillaIceMountains(float mountainWidth, float mountainStrength, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.terrainHeight = height;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainLonelyMountain(x, y, rtgWorld, river, this.strength, this.width, this.terrainHeight);
      }
   }
}
