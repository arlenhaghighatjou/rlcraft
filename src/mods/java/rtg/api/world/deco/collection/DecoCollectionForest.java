package rtg.api.world.deco.collection;

import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaPungens;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaSitchensis;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusPonderosa;

public class DecoCollectionForest extends DecoCollectionBase {
   private DecoTree.Distribution forestDistribution = new DecoTree.Distribution(100.0F, 6.0F, 0.8F);
   private float short1Min = -3.0F;
   private float short1Max = -1.0F;
   private float tallMin = -1.0F;
   private float tallMax = 3.0F;
   private float short2Min = 3.0F;
   private float short2Max = 5.0F;

   public DecoCollectionForest(BiomeConfig config) {
      super(config);
      this.addDeco(this.tallTrees(this.tallMin, this.tallMax)).addDeco(this.shortTrees(this.short1Min, this.short1Max)).addDeco(this.shortTrees(this.short2Min, this.short2Max)).addDeco(this.randomTrees()).addDeco(this.logs(), config.ALLOW_LOGS.get()).addDeco(this.shrubsOak()).addDeco(this.shrubsSpruce()).addDeco(this.flowers());
   }

   private DecoHelper5050 tallTrees(float noiseMin, float noiseMax) {
      return new DecoHelper5050(this.tallPineTrees(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), noiseMin, noiseMax), this.tallPineTrees(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE), noiseMin, noiseMax));
   }

   private DecoTree tallPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
      TreeRTG pinusPonderosa = new TreeRTGPinusPonderosa();
      pinusPonderosa.setLogBlock(log);
      pinusPonderosa.setLeavesBlock(leaves);
      pinusPonderosa.setMinTrunkSize(11);
      pinusPonderosa.setMaxTrunkSize(21);
      pinusPonderosa.setMinCrownSize(15);
      pinusPonderosa.setMaxCrownSize(29);
      this.addTree(pinusPonderosa);
      return (new DecoTree(pinusPonderosa)).setStrengthFactorForLoops(8.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(1).setMaxY(85);
   }

   private DecoHelper5050 shortTrees(float noiseMin, float noiseMax) {
      return new DecoHelper5050(this.shortPineTrees(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), noiseMin, noiseMax), this.shortPineTrees(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE), noiseMin, noiseMax));
   }

   private DecoTree shortPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
      TreeRTG piceaSitchensis = (new TreeRTGPiceaSitchensis()).setLogBlock(log).setLeavesBlock(leaves).setMinTrunkSize(4).setMaxTrunkSize(10).setMinCrownSize(6).setMaxCrownSize(14);
      this.addTree(piceaSitchensis);
      return (new DecoTree(piceaSitchensis)).setStrengthFactorForLoops(6.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(1).setMaxY(85);
   }

   private DecoHelperRandomSplit randomTrees() {
      return (new DecoHelperRandomSplit()).setDecos(new DecoBase[]{this.randomPungensTrees(), this.randomVanillaTrees()}).setChances(new int[]{8, 4});
   }

   private DecoTree randomPungensTrees() {
      TreeRTG piceaPungens = (new TreeRTGPiceaPungens()).setLogBlock(Blocks.LOG.getDefaultState()).setLeavesBlock(Blocks.LEAVES.getDefaultState()).setMinTrunkSize(2).setMaxTrunkSize(4).setMinCrownSize(5).setMaxCrownSize(8);
      this.addTree(piceaPungens);
      return (new DecoTree(piceaPungens)).setStrengthFactorForLoops(3.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE).setTreeConditionChance(5).setMaxY(100);
   }

   private DecoTree randomVanillaTrees() {
      WorldGenerator worldGenTrees = new WorldGenTrees(false);
      return (new DecoTree(worldGenTrees)).setStrengthFactorForLoops(3.0F).setTreeType(DecoTree.TreeType.WORLDGEN).setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE).setTreeConditionChance(3).setMaxY(120);
   }

   private DecoHelper5050 logs() {
      return new DecoHelper5050(this.oakLogs(), this.spruceLogs());
   }

   private DecoFallenTree oakLogs() {
      return (new DecoFallenTree()).setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE).setLogConditionChance(16).setMaxY(80).setLogBlock(Blocks.LOG.getDefaultState()).setLeavesBlock(Blocks.LEAVES.getDefaultState()).setMinSize(3).setMaxSize(6);
   }

   private DecoFallenTree spruceLogs() {
      return (new DecoFallenTree()).setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE).setLogConditionChance(24).setMaxY(80).setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE)).setMinSize(3).setMaxSize(6);
   }

   private DecoShrub shrubsOak() {
      return (new DecoShrub()).setMaxY(140).setLoopMultiplier(4.0F).setChance(3);
   }

   private DecoShrub shrubsSpruce() {
      return (new DecoShrub()).setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE)).setMaxY(140).setLoopMultiplier(4.0F).setChance(9);
   }

   private DecoFlowersRTG flowers() {
      return (new DecoFlowersRTG()).addFlowers(EnumFlowerType.POPPY, EnumFlowerType.BLUE_ORCHID, EnumFlowerType.ALLIUM, EnumFlowerType.HOUSTONIA, EnumFlowerType.RED_TULIP, EnumFlowerType.ORANGE_TULIP, EnumFlowerType.WHITE_TULIP, EnumFlowerType.PINK_TULIP, EnumFlowerType.OXEYE_DAISY, EnumFlowerType.DANDELION).setMaxY(128).setStrengthFactor(6.0F);
   }
}
