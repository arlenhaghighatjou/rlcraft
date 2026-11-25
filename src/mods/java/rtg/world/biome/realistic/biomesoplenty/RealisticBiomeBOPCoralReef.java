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
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPCoralReef extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.coral_reef.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPCoralReef() {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPCoralReef.TerrainBOPCoralReef(false, -10.0F, 0.0F, 0.0F, 0.0F, 30.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPCoralReef.SurfaceBOPCoralReef(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public static class SurfaceBOPCoralReef extends SurfaceBase {
      public SurfaceBOPCoralReef(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
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

   public static class TerrainBOPCoralReef extends TerrainBase {
      private boolean booRiver;
      private float[] height;
      private int heightLength;
      private float strength;
      private float cWidth;
      private float cHeigth;
      private float cStrength;
      private float base;

      public TerrainBOPCoralReef(boolean riverGen, float heightStrength, float canyonWidth, float canyonHeight, float canyonStrength, float baseHeight) {
         this.booRiver = riverGen;
         this.height = new float[]{5.0F, 0.5F, 12.5F, 0.5F};
         this.strength = heightStrength;
         this.heightLength = this.height.length;
         this.cWidth = canyonWidth;
         this.cHeigth = canyonHeight;
         this.cStrength = canyonStrength;
         this.base = baseHeight;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainOceanCanyon(x, y, rtgWorld, river, this.height, border, this.strength, this.heightLength, this.booRiver);
      }
   }
}
