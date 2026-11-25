package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoCrop;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelperThisOrThat;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGQuercusRobur;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeVanillaPlains extends RealisticBiomeBase {
   public static Biome biome = Biomes.PLAINS;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaPlains() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_VILLAGES.set(true);
      this.getConfig().addProperty(this.getConfig().ALLOW_WHEAT).set(true);
      this.getConfig().addProperty(this.getConfig().WHEAT_CHANCE).set(50);
      this.getConfig().addProperty(this.getConfig().WHEAT_MIN_Y).set(63);
      this.getConfig().addProperty(this.getConfig().WHEAT_MAX_Y).set(255);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaPlains.TerrainVanillaPlains();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaPlains.SurfaceVanillaPlains(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public void initDecos() {
      DecoCrop decoCropWheat = new DecoCrop();
      decoCropWheat.setSize(4);
      decoCropWheat.setDensity(4);
      decoCropWheat.setChance(this.getConfig().WHEAT_CHANCE.get());
      decoCropWheat.setType(3);
      decoCropWheat.setWater(false);
      decoCropWheat.setMinY(this.getConfig().WHEAT_MIN_Y.get());
      decoCropWheat.setMaxY(this.getConfig().WHEAT_MAX_Y.get());
      this.addDeco(decoCropWheat, this.getConfig().ALLOW_WHEAT.get());
      DecoShrub decoShrubOak = new DecoShrub();
      decoShrubOak.setLogBlock(Blocks.LOG.getDefaultState());
      decoShrubOak.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      decoShrubOak.setMaxY(110);
      decoShrubOak.setLoops(1);
      decoShrubOak.setChance(36);
      this.addDeco(decoShrubOak);
      DecoFlowersRTG decoFlowersRTG = (new DecoFlowersRTG()).addFlowers(EnumFlowerType.POPPY, EnumFlowerType.BLUE_ORCHID, EnumFlowerType.ALLIUM, EnumFlowerType.HOUSTONIA, EnumFlowerType.RED_TULIP, EnumFlowerType.ORANGE_TULIP, EnumFlowerType.WHITE_TULIP, EnumFlowerType.PINK_TULIP, EnumFlowerType.OXEYE_DAISY, EnumFlowerType.DANDELION).setMaxY(128);
      this.addDeco(decoFlowersRTG);
      TreeRTG roburTree1 = new TreeRTGQuercusRobur();
      roburTree1.setLogBlock(Blocks.LOG.getDefaultState());
      roburTree1.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      roburTree1.setMinTrunkSize(3);
      roburTree1.setMaxTrunkSize(5);
      roburTree1.setMinCrownSize(7);
      roburTree1.setMaxCrownSize(9);
      this.addTree(roburTree1);
      DecoTree oakTrees = new DecoTree(roburTree1);
      oakTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      oakTrees.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      oakTrees.setDistribution(new DecoTree.Distribution(100.0F, 6.0F, 0.8F));
      oakTrees.setTreeConditionNoise(0.4F);
      oakTrees.setTreeConditionChance(48);
      TreeRTG roburTree2 = new TreeRTGQuercusRobur();
      roburTree2.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      roburTree2.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      roburTree2.setMinTrunkSize(3);
      roburTree2.setMaxTrunkSize(5);
      roburTree2.setMinCrownSize(7);
      roburTree2.setMaxCrownSize(9);
      this.addTree(roburTree2);
      DecoTree birchTrees = new DecoTree(roburTree2);
      birchTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      birchTrees.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      birchTrees.setDistribution(new DecoTree.Distribution(100.0F, 6.0F, 0.8F));
      birchTrees.setTreeConditionNoise(0.4F);
      birchTrees.setTreeConditionChance(48);
      this.addDeco(new DecoHelperThisOrThat(4, DecoHelperThisOrThat.ChanceType.NOT_EQUALS_ZERO, oakTrees, birchTrees));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.grassPerChunk = 5;
      this.baseBiome().decorator.flowersPerChunk = -999;
      this.baseBiome().decorator.treesPerChunk = -999;
   }

   public boolean overridesHardcoded() {
      return true;
   }

   public static class SurfaceVanillaPlains extends SurfaceBase {
      public SurfaceVanillaPlains(BiomeConfig config, IBlockState top, IBlockState filler) {
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

   public static class TerrainVanillaPlains extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaPlains() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}
