package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import java.util.Random;
import net.minecraft.block.Block;
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
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGBetulaPapyrifera;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaSitchensis;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.JitterEffect;

public class RealisticBiomeBOPLandOfLakes extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.land_of_lakes.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPLandOfLakes() {
      super(biome);
   }

   public Biome preferredBeach() {
      return (Biome)BOPBiomes.gravel_beach.orNull();
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPLandOfLakes.TerrainBOPLandOfLakes();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPLandOfLakes.SurfaceBOPLandOfLakes(this.getConfig(), Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, Blocks.GRASS.getDefaultState(), 0.1F);
   }

   public void initDecos() {
      TreeRTG birchTree = new TreeRTGBetulaPapyrifera();
      birchTree.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      birchTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      birchTree.setMinTrunkSize(4);
      birchTree.setMaxTrunkSize(10);
      birchTree.setMinCrownSize(8);
      birchTree.setMaxCrownSize(19);
      this.addTree(birchTree);
      DecoTree birchTrees = new DecoTree(birchTree);
      birchTrees.setStrengthFactorForLoops(9.0F);
      birchTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      birchTrees.getDistribution().setNoiseDivisor(100.0F);
      birchTrees.getDistribution().setNoiseFactor(6.0F);
      birchTrees.getDistribution().setNoiseAddend(0.8F);
      birchTrees.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      birchTrees.setTreeConditionChance(1);
      birchTrees.setTreeConditionNoise(0.0F);
      birchTrees.setMaxY(120);
      TreeRTG sitchensisTree = new TreeRTGPiceaSitchensis();
      sitchensisTree.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      sitchensisTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      sitchensisTree.setMinTrunkSize(4);
      sitchensisTree.setMaxTrunkSize(9);
      sitchensisTree.setMinCrownSize(5);
      sitchensisTree.setMaxCrownSize(14);
      this.addTree(sitchensisTree);
      DecoTree smallPine = new DecoTree(sitchensisTree);
      smallPine.setStrengthFactorForLoops(9.0F);
      smallPine.setTreeType(DecoTree.TreeType.RTG_TREE);
      smallPine.getDistribution().setNoiseDivisor(100.0F);
      smallPine.getDistribution().setNoiseFactor(6.0F);
      smallPine.getDistribution().setNoiseAddend(0.8F);
      smallPine.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      smallPine.setTreeConditionChance(1);
      smallPine.setTreeConditionNoise(0.0F);
      smallPine.setMaxY(120);
      new DecoHelper5050(birchTrees, smallPine);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(12);
      decoFallenTree.setRandomLogBlocks(new IBlockState[]{Blocks.LOG.getDefaultState(), BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLog(EnumType.BIRCH)});
      decoFallenTree.setMinSize(8);
      decoFallenTree.setMaxSize(12);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setMaxY(110);
      decoShrub.setLoopMultiplier(3.0F);
      this.addDeco(decoShrub);
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setMaxY(80);
      decoBoulder.setChance(12);
      decoBoulder.setStrengthFactor(1.0F);
      this.addDeco(decoBoulder);
   }

   public static class SurfaceBOPLandOfLakes extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mix;
      private float mixHeight;

      public SurfaceBOPLandOfLakes(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mixBlock, float mixSize) {
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

   public static class TerrainBOPLandOfLakes extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;
      private HeightVariation small;
      private HeightVariation large;
      private JitterEffect largeJitter;
      private JitterEffect smallJitter;

      public TerrainBOPLandOfLakes() {
         super(63.0F);
         this.small = new HeightVariation();
         this.small.height = 2.5F;
         this.small.octave = 1;
         this.small.wavelength = 10.0F;
         this.large = new HeightVariation();
         this.large.height = 5.0F;
         this.large.octave = 2;
         this.large.wavelength = 20.0F;
         this.smallJitter = new JitterEffect();
         this.smallJitter.amplitude = 2.0F;
         this.smallJitter.wavelength = 9.0F;
         this.smallJitter.jittered = this.large.plus(this.small);
         this.largeJitter = new JitterEffect();
         this.largeJitter.amplitude = 4.0F;
         this.largeJitter.wavelength = 18.0F;
         this.largeJitter.jittered = this.smallJitter;
      }

      public TerrainBOPLandOfLakes(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.largeJitter.added(rtgWorld, (float)x, (float)y) + this.base, river);
      }
   }
}
