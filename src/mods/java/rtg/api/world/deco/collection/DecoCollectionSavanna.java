package rtg.api.world.deco.collection;

import net.minecraft.init.Blocks;
import rtg.api.config.BiomeConfig;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoDoubleGrass;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGAcaciaBucheri;

public class DecoCollectionSavanna extends DecoCollectionBase {
   public DecoCollectionSavanna(BiomeConfig config) {
      super(config);
      DecoShrub acaciaShrub = new DecoShrub();
      acaciaShrub.setLogBlock(Blocks.LOG2.getDefaultState());
      acaciaShrub.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      acaciaShrub.setMaxY(160);
      acaciaShrub.setLoopMultiplier(2.0F);
      acaciaShrub.setChance(12);
      this.addDeco(acaciaShrub);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.setLoops(1);
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(36);
      decoFallenTree.setLogBlock(Blocks.LOG2.getDefaultState());
      decoFallenTree.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(6);
      this.addDeco(decoFallenTree, config.ALLOW_LOGS.get());
      TreeRTG bucheriTree = new TreeRTGAcaciaBucheri();
      bucheriTree.setLogBlock(Blocks.LOG2.getDefaultState());
      bucheriTree.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      bucheriTree.setMinTrunkSize(4);
      bucheriTree.setMaxTrunkSize(9);
      this.addTree(bucheriTree);
      DecoTree bucheriTrees = new DecoTree(bucheriTree);
      bucheriTrees.setLoops(1);
      bucheriTrees.setTreeType(DecoTree.TreeType.RTG_TREE);
      bucheriTrees.getDistribution().setNoiseDivisor(80.0F);
      bucheriTrees.getDistribution().setNoiseFactor(60.0F);
      bucheriTrees.getDistribution().setNoiseAddend(-15.0F);
      bucheriTrees.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      bucheriTrees.setTreeConditionNoise(-0.4F);
      bucheriTrees.setTreeConditionChance(28);
      this.addDeco(bucheriTrees);
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setChance(32);
      decoBoulder.setMaxY(95);
      this.addDeco(decoBoulder);
      DecoDoubleGrass decoDoubleGrass = new DecoDoubleGrass();
      decoDoubleGrass.setMaxY(128);
      decoDoubleGrass.setStrengthFactor(3.0F);
      this.addDeco(decoDoubleGrass);
   }
}
