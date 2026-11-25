package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPGrove extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.grove.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPGrove() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPGrove.TerrainBOPGrove();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPGrove.SurfaceBOPGrove(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, BlockUtil.getStateDirt(DirtType.PODZOL), 0.15F);
   }

   public void initDecos() {
      DecoShrub decoShrubCustom = new DecoShrub();
      decoShrubCustom.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      decoShrubCustom.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      decoShrubCustom.setMaxY(110);
      decoShrubCustom.setLoopMultiplier(2.0F);
      DecoShrub decoShrubCustom2 = new DecoShrub();
      decoShrubCustom2.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      decoShrubCustom2.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      decoShrubCustom2.setMaxY(110);
      decoShrubCustom2.setLoopMultiplier(2.0F);
      DecoHelper5050 decoHelperHelper50502 = new DecoHelper5050(decoShrubCustom, decoShrubCustom2);
      this.addDeco(decoHelperHelper50502);
      DecoFallenTree decoFallenTree1 = new DecoFallenTree();
      decoFallenTree1.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree1.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree1.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree1.setLogConditionChance(3);
      decoFallenTree1.setMaxY(100);
      decoFallenTree1.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      decoFallenTree1.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      decoFallenTree1.setMinSize(3);
      decoFallenTree1.setMaxSize(6);
      DecoFallenTree decoFallenTree2 = new DecoFallenTree();
      decoFallenTree2.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree2.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree2.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree2.setLogConditionChance(3);
      decoFallenTree2.setMaxY(100);
      decoFallenTree2.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      decoFallenTree2.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      decoFallenTree2.setMinSize(3);
      decoFallenTree2.setMaxSize(6);
      DecoHelper5050 decoHelperHelper5050 = new DecoHelper5050(decoFallenTree1, decoFallenTree2);
      this.addDeco(decoHelperHelper5050, this.getConfig().ALLOW_LOGS.get());
   }

   public static class SurfaceBOPGrove extends SurfaceBase {
      public byte mixByte = 0;
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mix;
      private float mixHeight;

      public SurfaceBOPGrove(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mixBlock, float mixSize) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mix = mixBlock;
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
                     primer.setBlockState(x, k, z, this.mix);
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

   public static class TerrainBOPGrove extends TerrainBase {
      private float baseHeight = 64.0F;
      private float peakyHillWavelength = 40.0F;
      private float peakyHillStrength = 5.0F;
      private float smoothHillWavelength = 20.0F;
      private float smoothHillStrength = 10.0F;

      public TerrainBOPGrove() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainGrasslandHills(x, y, rtgWorld, river, this.smoothHillWavelength, this.smoothHillStrength, this.peakyHillWavelength, this.peakyHillStrength, this.baseHeight);
      }
   }
}
