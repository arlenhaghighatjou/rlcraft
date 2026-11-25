package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
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
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPFen extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.fen.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPFen() {
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
      return new RealisticBiomeBOPFen.TerrainBOPFen();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPFen.SurfaceBOPFen(this.getConfig(), biome.topBlock, biome.fillerBlock, biome.topBlock, biome.fillerBlock, 80.0F, -0.15F, 10.0F, 0.5F);
   }

   public void initDecos() {
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setMaxY(80);
      decoBoulder.setChance(12);
      decoBoulder.setStrengthFactor(1.0F);
      this.addDeco(decoBoulder);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(16);
      decoFallenTree.setLogBlock(BOPBlocks.log_3.getStateFromMeta(2));
      decoFallenTree.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(5);
      DecoFallenTree decoFallenTree2 = new DecoFallenTree();
      decoFallenTree2.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree2.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree2.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree2.setLogConditionChance(16);
      decoFallenTree2.setMaxY(100);
      decoFallenTree2.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      decoFallenTree2.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      decoFallenTree2.setMinSize(3);
      decoFallenTree2.setMaxSize(5);
      DecoHelperRandomSplit decoHelperRandomSplit = new DecoHelperRandomSplit();
      decoHelperRandomSplit.decos = new DecoBase[]{decoFallenTree, decoFallenTree2};
      decoHelperRandomSplit.chances = new int[]{4, 1};
      this.addDeco(decoHelperRandomSplit, this.getConfig().ALLOW_LOGS.get());
   }

   public static class SurfaceBOPFen extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceBOPFen(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.blockMixTop = mixTop;
         this.blockMixFiller = mixFiller;
         this.floMixWidth = mixWidth;
         this.floMixHeight = mixHeight;
         this.floSmallWidth = smallWidth;
         this.floSmallStrength = smallStrength;
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

   public static class TerrainBOPFen extends TerrainBase {
      public TerrainBOPFen() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainMarsh(x, y, rtgWorld, 61.5F, river);
      }
   }
}
