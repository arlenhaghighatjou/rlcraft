package rtg.world.biome.realistic.abyssalcraft;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeACDarklandsHighland extends RealisticBiomeACBase {
   public static Biome biome = ACBiomes.darklands_hills;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeACDarklandsHighland() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeACDarklandsHighland.TerrainACDarklandsHighland(10.0F, 120.0F, 10.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeACDarklandsHighland.SurfaceACDarklandsHighland(this.getConfig(), biome.topBlock, biome.fillerBlock, biome.topBlock, biome.fillerBlock, 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public static class SurfaceACDarklandsHighland extends SurfaceACBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceACDarklandsHighland(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.mixBlockTop = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mixTop);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), mixFill);
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

   public static class TerrainACDarklandsHighland extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainACDarklandsHighland(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base);
      }
   }
}
