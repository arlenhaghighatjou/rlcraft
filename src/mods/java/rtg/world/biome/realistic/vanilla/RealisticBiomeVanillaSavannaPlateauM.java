package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.PlateauUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoCactus;
import rtg.api.world.deco.DecoDoubleGrass;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGAcaciaBucheri;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaSavannaPlateauM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_SAVANNA_ROCK;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaSavannaPlateauM() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().ALLOW_PLATEAU_MODIFICATIONS).set(true);
      this.getConfig().addProperty(this.getConfig().PLATEAU_GRADIENT_BLOCK_LIST).set(PlateauUtil.getSavannaPlateauBlocks());
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaSavannaPlateauM.TerrainVanillaSavannaPlateauM(true, 35.0F, 160.0F, 60.0F, 40.0F, 69.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaSavannaPlateauM.SurfaceVanillaSavannaPlateauM(this.getConfig(), biome.topBlock, biome.fillerBlock, 0);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      DecoBoulder decoBoulder1 = new DecoBoulder();
      decoBoulder1.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder1.setMaxY(80);
      decoBoulder1.setChance(24);
      this.addDeco(decoBoulder1);
      DecoBoulder decoBoulder2 = new DecoBoulder();
      decoBoulder2.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder1.setMinY(110);
      decoBoulder2.setChance(24);
      this.addDeco(decoBoulder2);
      DecoShrub acaciaShrub = new DecoShrub();
      acaciaShrub.setLogBlock(Blocks.LOG2.getDefaultState());
      acaciaShrub.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      acaciaShrub.setMaxY(160);
      acaciaShrub.setLoopMultiplier(3.0F);
      acaciaShrub.setChance(9);
      this.addDeco(acaciaShrub);
      TreeRTG acaciaTree = new TreeRTGAcaciaBucheri();
      acaciaTree.setLogBlock(Blocks.LOG2.getDefaultState());
      acaciaTree.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      acaciaTree.setMinTrunkSize(12);
      acaciaTree.setMaxTrunkSize(16);
      this.addTree(acaciaTree);
      DecoTree acaciaTrees = new DecoTree(acaciaTree);
      acaciaTrees.setStrengthFactorForLoops(2.0F);
      acaciaTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      acaciaTrees.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      acaciaTrees.setTreeConditionChance(12);
      acaciaTrees.setMaxY(90);
      this.addDeco(acaciaTrees);
      DecoCactus decoCactus = new DecoCactus();
      decoCactus.setMaxY(160);
      decoCactus.setLoops(60);
      decoCactus.setChance(8);
      this.addDeco(decoCactus, this.getConfig().ALLOW_CACTUS.get());
      DecoDoubleGrass decoDoubleGrass = new DecoDoubleGrass();
      decoDoubleGrass.setMaxY(128);
      decoDoubleGrass.setStrengthFactor(3.0F);
      this.addDeco(decoDoubleGrass);
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public class SurfaceVanillaSavannaPlateauM extends SurfaceBase {
      private int grassRaise = 0;
      private IBlockState mixBlock;

      public SurfaceVanillaSavannaPlateauM(BiomeConfig config, IBlockState top, IBlockState fill, int grassHeight) {
         super(config, top, fill);
         this.grassRaise = grassHeight;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), BlockUtil.getStateDirt(DirtType.COARSE_DIRT));
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.3F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  primer.setBlockState(x, k, z, PlateauUtil.getPlateauBand(rtgWorld, RealisticBiomeVanillaSavannaPlateauM.this, i, k, j));
               } else if (k > 74 + this.grassRaise) {
                  if (depth == 0) {
                     if (rand.nextInt(5) == 0) {
                        primer.setBlockState(x, k, z, this.mixBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               } else if (depth == 0 && k > 61) {
                  int r = (int)((float)(k - (62 + this.grassRaise)) / 2.0F);
                  if (rand.nextInt(r + 2) == 0) {
                     primer.setBlockState(x, k, z, this.topBlock);
                  } else if (rand.nextInt((int)((float)r / 2.0F) + 2) == 0) {
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

   public static class TerrainVanillaSavannaPlateauM extends TerrainBase {
      private float[] height = new float[]{18.0F, 0.4F, 12.0F, 0.6F, 8.0F, 0.8F};
      private int heightLength;
      private float strength = 10.0F;

      public TerrainVanillaSavannaPlateauM(boolean riverGen, float heightStrength, float canyonWidth, float canyonHeight, float canyonStrength, float baseHeight) {
         this.heightLength = this.height.length;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlateau((float)x, (float)y, rtgWorld, river, this.height, border, this.strength, this.heightLength, 50.0F, true);
      }
   }
}
