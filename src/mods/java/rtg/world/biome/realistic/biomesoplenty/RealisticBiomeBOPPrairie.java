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
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPPrairie extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.prairie.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPPrairie() {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPPrairie.TerrainBOPPrairie(65.0F, 80.0F, 25.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPPrairie.SurfaceBOPPrairie(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public static class SurfaceBOPPrairie extends SurfaceBase {
      public SurfaceBOPPrairie(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
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
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainBOPPrairie extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;

      public TerrainBOPPrairie(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 200.0F, 1.0F, 30.0F, 1.0F, this.maxHeight);
      }
   }
}
