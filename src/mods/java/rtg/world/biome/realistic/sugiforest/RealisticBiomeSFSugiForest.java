package rtg.world.biome.realistic.sugiforest;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeSFSugiForest extends RealisticBiomeSFBase {
   private static IBlockState sugiLogBlock = BlockUtil.getBlockStateFromCfgString("sugiforest:sugi_log", Blocks.LOG.getDefaultState());
   private static IBlockState sugiLeavesBlock = BlockUtil.getBlockStateFromCfgString("sugiforest:sugi_leaves", Blocks.LEAVES.getDefaultState());

   public RealisticBiomeSFSugiForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeSFSugiForest.TerrainSFSugiForest();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeSFSugiForest.SurfaceSFSugiForest(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, this.baseBiome().topBlock, this.baseBiome().fillerBlock, 80.0F, -0.15F, 10.0F, 0.5F);
   }

   public void initDecos() {
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(24);
      decoFallenTree.setLogBlock(sugiLogBlock);
      decoFallenTree.setLeavesBlock(sugiLeavesBlock);
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(6);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub decoShrubSugi = new DecoShrub();
      decoShrubSugi.setLogBlock(sugiLogBlock);
      decoShrubSugi.setLeavesBlock(sugiLeavesBlock);
      decoShrubSugi.setMaxY(90);
      decoShrubSugi.setLoopMultiplier(4.0F);
      decoShrubSugi.setChance(8);
      this.addDeco(decoShrubSugi);
      DecoShrub decoShrubOak = new DecoShrub();
      decoShrubOak.setMaxY(90);
      decoShrubOak.setLoopMultiplier(4.0F);
      decoShrubOak.setChance(4);
      this.addDeco(decoShrubOak);
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setChance(24);
      decoBoulder.setMaxY(80);
      decoBoulder.setStrengthFactor(2.0F);
      this.addDeco(decoBoulder);
   }

   public static class SurfaceSFSugiForest extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceSFSugiForest(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.blockMixTop = mixTop;
         this.blockMixFiller = mixFiller;
         this.floMixWidth = mixWidth;
         this.floMixHeight = mixHeight;
         this.floSmallWidth = smallWidth;
         this.floSmallStrength = smallStrength;
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
                  if (simplex.noise2f((float)i / this.floMixWidth, (float)j / this.floMixWidth) + simplex.noise2f((float)i / this.floSmallWidth, (float)j / this.floSmallWidth) * this.floSmallStrength > this.floMixHeight) {
                     primer.setBlockState(x, k, z, this.blockMixTop);
                     mix = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  if (mix) {
                     primer.setBlockState(x, k, z, this.blockMixFiller);
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainSFSugiForest extends TerrainBase {
      private float baseHeight = 72.0F;
      private float peakyHillWavelength = 40.0F;
      private float peakyHillStrength = 10.0F;
      private float smoothHillWavelength = 20.0F;
      private float smoothHillStrength = 20.0F;

      public TerrainSFSugiForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundNoiseAmplitudeHills, rtgWorld);
         float h = terrainGrasslandHills(x, y, rtgWorld, river, this.peakyHillWavelength, this.peakyHillStrength, this.smoothHillWavelength, this.smoothHillStrength, this.baseHeight);
         return riverized(this.groundNoise + h, river);
      }
   }
}
