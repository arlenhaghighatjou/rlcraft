package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
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

public class RealisticBiomeBOPBrushland extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.brushland.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPBrushland() {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPBrushland.TerrainBOPBrushland();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPBrushland.SurfaceBOPBrushland(this.getConfig(), biome.topBlock, biome.fillerBlock, Blocks.SAND.getDefaultState(), 13.0F, 0.27F);
   }

   public static class SurfaceBOPBrushland extends SurfaceBase {
      private IBlockState mixBlock;
      private float width;
      private float height;

      public SurfaceBOPBrushland(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mix, float mixWidth, float mixHeight) {
         super(config, top, filler);
         this.mixBlock = mix;
         this.width = mixWidth;
         this.height = mixHeight;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

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
                  if (simplex.noise2f((float)i / this.width, (float)j / this.width) > this.height) {
                     primer.setBlockState(x, k, z, this.mixBlock);
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

   public static class TerrainBOPBrushland extends TerrainBase {
      private float baseHeight = 76.0F;
      private float hillStrength = 20.0F;

      public TerrainBOPBrushland() {
      }

      public TerrainBOPBrushland(float bh, float hs) {
         this.baseHeight = bh;
         this.hillStrength = hs;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundNoiseAmplitudeHills, rtgWorld);
         float m = hills((float)x, (float)y, this.hillStrength, rtgWorld);
         return riverized(this.baseHeight + this.groundNoise + m, river);
      }
   }
}
