package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoCobwebs;
import rtg.api.world.deco.DecoDeadBush;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoMushrooms;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.DecoWorldGen;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.deco.helper.DecoHelperThisOrThat;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGAcaciaBucheri;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGCeibaPentandra;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGRhizophoraMucronata;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeVanillaRoofedForest extends RealisticBiomeBase {
   public static Biome biome = Biomes.ROOFED_FOREST;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaRoofedForest() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.5F);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().ALLOW_COBWEBS).set(true);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaRoofedForest.TerrainVanillaRoofedForest();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaRoofedForest.SurfaceVanillaRoofedForest(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, BlockUtil.getStateDirt(DirtType.PODZOL), 0.15F);
   }

   public void initDecos() {
      DecoMushrooms decoMushrooms = new DecoMushrooms();
      decoMushrooms.setChance(4);
      decoMushrooms.setMaxY(90);
      decoMushrooms.setRandomType(DecoMushrooms.RandomType.ALWAYS_GENERATE);
      this.addDeco(decoMushrooms);
      TreeRTG mucronataTree = new TreeRTGRhizophoraMucronata(3, 4, 13.0F, 0.32F, 0.1F);
      mucronataTree.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      mucronataTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      mucronataTree.setMinTrunkSize(2);
      mucronataTree.setMaxTrunkSize(3);
      mucronataTree.setMinCrownSize(9);
      mucronataTree.setMaxCrownSize(18);
      mucronataTree.setNoLeaves(false);
      this.addTree(mucronataTree);
      DecoTree mangroveTree = new DecoTree(mucronataTree);
      mangroveTree.setTreeType(DecoTree.TreeType.RTG_TREE);
      mangroveTree.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      mangroveTree.setTreeConditionChance(1);
      mangroveTree.setStrengthFactorForLoops(14.0F);
      mangroveTree.setMaxY(110);
      TreeRTG pentandraTree = new TreeRTGCeibaPentandra(13.0F, 3, 0.32F, 0.1F);
      pentandraTree.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      pentandraTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      pentandraTree.setMinTrunkSize(2);
      pentandraTree.setMaxTrunkSize(3);
      pentandraTree.setMinCrownSize(9);
      pentandraTree.setMaxCrownSize(18);
      pentandraTree.setNoLeaves(false);
      this.addTree(pentandraTree);
      DecoTree ceibaPentandraTree = new DecoTree(pentandraTree);
      ceibaPentandraTree.setTreeType(DecoTree.TreeType.RTG_TREE);
      ceibaPentandraTree.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      ceibaPentandraTree.setTreeConditionChance(1);
      ceibaPentandraTree.setStrengthFactorForLoops(14.0F);
      ceibaPentandraTree.setMaxY(110);
      TreeRTG bucheriTree = new TreeRTGAcaciaBucheri();
      bucheriTree.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      bucheriTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      bucheriTree.setMinTrunkSize(6);
      bucheriTree.setMaxTrunkSize(14);
      this.addTree(bucheriTree);
      DecoTree bucheriTrees = new DecoTree(bucheriTree);
      bucheriTrees.setLoops(2);
      bucheriTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      bucheriTrees.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      bucheriTrees.setTreeConditionChance(1);
      DecoHelperRandomSplit decoHelperRandomSplit = new DecoHelperRandomSplit();
      decoHelperRandomSplit.decos = new DecoBase[]{mangroveTree, ceibaPentandraTree, bucheriTrees};
      decoHelperRandomSplit.chances = new int[]{11, 11, 2};
      this.addDeco(decoHelperRandomSplit);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree.setLogConditionChance(16);
      decoFallenTree.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      decoFallenTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      decoFallenTree.setMinSize(4);
      decoFallenTree.setMaxSize(9);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub darkOakShrub = new DecoShrub();
      darkOakShrub.setLogBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      darkOakShrub.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.DARK_OAK));
      darkOakShrub.setMaxY(100);
      darkOakShrub.setLoopMultiplier(6.0F);
      DecoShrub oakShrub = new DecoShrub();
      oakShrub.setLogBlock(Blocks.LOG.getDefaultState());
      oakShrub.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      oakShrub.setMaxY(100);
      oakShrub.setLoopMultiplier(6.0F);
      this.addDeco(new DecoHelperThisOrThat(4, DecoHelperThisOrThat.ChanceType.NOT_EQUALS_ZERO, darkOakShrub, oakShrub));
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.MOSSY_COBBLESTONE.getDefaultState());
      decoBoulder.setChance(16);
      decoBoulder.setMaxY(80);
      decoBoulder.setStrengthFactor(2.0F);
      this.addDeco(decoBoulder);
      DecoCobwebs decoCobwebs = new DecoCobwebs();
      decoCobwebs.setChance(1);
      decoCobwebs.setMinY(63);
      decoCobwebs.setMaxY(76);
      decoCobwebs.setStrengthFactor(24.0F);
      decoCobwebs.setAdjacentBlock(BlockUtil.getStateLog(EnumType.DARK_OAK));
      decoCobwebs.setMinAdjacents(2);
      this.addDeco(decoCobwebs, this.getConfig().ALLOW_COBWEBS.get());
      DecoDeadBush decoDeadBush = new DecoDeadBush();
      decoDeadBush.setMaxY(100);
      decoDeadBush.setChance(2);
      decoDeadBush.setStrengthFactor(2.0F);
      this.addDeco(decoDeadBush);
      DecoWorldGen decoBigShroom = new DecoWorldGen(new WorldGenBigMushroom(), EventType.BIG_SHROOM);
      decoBigShroom.setMinY(63);
      decoBigShroom.setMaxY(100);
      decoBigShroom.setChance(8);
      this.addDeco(decoBigShroom);
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.grassPerChunk = 3;
      this.baseBiome().decorator.flowersPerChunk = 1;
   }

   public boolean overridesHardcoded() {
      return true;
   }

   public static class SurfaceVanillaRoofedForest extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceVanillaRoofedForest(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
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

   public static class TerrainVanillaRoofedForest extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaRoofedForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
