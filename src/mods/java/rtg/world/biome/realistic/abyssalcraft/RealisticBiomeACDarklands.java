package rtg.world.biome.realistic.abyssalcraft;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
import com.shinoow.abyssalcraft.api.block.ACBlocks;
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
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeACDarklands extends RealisticBiomeACBase {
   public static Biome biome = ACBiomes.darklands;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeACDarklands() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeACDarklands.TerrainACDarklands();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeACDarklands.SurfaceACDarklands(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, biome.topBlock, 0.15F);
   }

   public void initDecos() {
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenTree.setLogConditionChance(8);
      decoFallenTree.setLogBlock(ACBlocks.darklands_oak_wood.getDefaultState());
      decoFallenTree.setLeavesBlock(ACBlocks.darklands_oak_leaves.getDefaultState());
      decoFallenTree.setMinSize(2);
      decoFallenTree.setMaxSize(3);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub decoShrubCustom = new DecoShrub();
      decoShrubCustom.setLogBlock(ACBlocks.darklands_oak_wood.getDefaultState());
      decoShrubCustom.setLeavesBlock(ACBlocks.darklands_oak_leaves.getDefaultState());
      decoShrubCustom.setMaxY(110);
      decoShrubCustom.setNotEqualsZeroChance(3);
      decoShrubCustom.setLoopMultiplier(2.0F);
      this.addDeco(decoShrubCustom);
   }

   public static class SurfaceACDarklands extends SurfaceACBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceACDarklands(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.mixHeight = mixSize;
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
                  } else if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > this.mixHeight) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                     m = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
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

   public static class TerrainACDarklands extends TerrainBase {
      private float hillStrength;

      public TerrainACDarklands() {
         this(72.0F, 40.0F);
      }

      public TerrainACDarklands(float bh, float hs) {
         this.hillStrength = 40.0F;
         this.base = bh;
         this.hillStrength = hs;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, 10.0F, 68.0F, this.hillStrength, this.base - 62.0F);
      }
   }
}
