package rtg.world.biome.realistic.vanilla;

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
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoMushrooms;
import rtg.api.world.deco.DecoPumpkin;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusNigra;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaExtremeHillsEdge extends RealisticBiomeBase {
   public static Biome biome = Biomes.EXTREME_HILLS_EDGE;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaExtremeHillsEdge() {
      super(biome, RealisticBiomeBase.BeachType.STONE);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaExtremeHills.RidgedExtremeHills(125.0F, 67.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaExtremeHillsEdge.SurfaceVanillaExtremeHillsEdge(this.getConfig(), biome.topBlock, biome.fillerBlock, Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F);
   }

   public void initDecos() {
      TreeRTG nigraTree = new TreeRTGPinusNigra();
      nigraTree.setLogBlock(Blocks.LOG.getDefaultState());
      nigraTree.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      nigraTree.setMinTrunkSize(18);
      nigraTree.setMaxTrunkSize(27);
      nigraTree.setMinCrownSize(7);
      nigraTree.setMaxCrownSize(10);
      this.addTree(nigraTree);
      DecoTree decoTrees = new DecoTree(nigraTree);
      decoTrees.setStrengthFactorForLoops(4.0F);
      decoTrees.setStrengthNoiseFactorXForLoops(true);
      decoTrees.getDistribution().setNoiseDivisor(100.0F);
      decoTrees.getDistribution().setNoiseFactor(6.0F);
      decoTrees.getDistribution().setNoiseAddend(0.8F);
      decoTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      decoTrees.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      decoTrees.setTreeConditionChance(24);
      decoTrees.setMaxY(100);
      this.addDeco(decoTrees);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(6);
      decoFallenTree.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      decoFallenTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(6);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setMaxY(100);
      decoShrub.setLoopMultiplier(2.0F);
      this.addDeco(decoShrub);
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.MOSSY_COBBLESTONE.getDefaultState());
      decoBoulder.setChance(12);
      decoBoulder.setMaxY(95);
      decoBoulder.setStrengthFactor(2.0F);
      this.addDeco(decoBoulder);
      DecoMushrooms decoMushrooms = new DecoMushrooms();
      decoMushrooms.setMaxY(90);
      decoMushrooms.setRandomFloat(3.0F);
      this.addDeco(decoMushrooms);
      DecoPumpkin decoPumpkin = new DecoPumpkin();
      decoPumpkin.setMaxY(90);
      decoPumpkin.setRandomFloat(20.0F);
      this.addDeco(decoPumpkin);
   }

   public static class SurfaceVanillaExtremeHillsEdge extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceVanillaExtremeHillsEdge(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
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

   public static class TerrainVanillaExtremeHillsEdge extends TerrainBase {
      private float start;
      private float height;
      private float base;
      private float width;

      public TerrainVanillaExtremeHillsEdge(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, 10.0F);
      }
   }
}
