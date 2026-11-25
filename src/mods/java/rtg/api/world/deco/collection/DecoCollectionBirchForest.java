package rtg.api.world.deco.collection;

import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.world.gen.feature.WorldGenTrees;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelperRandomSplit;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGBetulaPapyrifera;

public class DecoCollectionBirchForest extends DecoCollectionBase {
   public DecoCollectionBirchForest(BiomeConfig config) {
      super(config);
      this.addDeco(this.tallBirchTrees()).addDeco(this.randomTrees()).addDeco(this.logs(), config.ALLOW_LOGS.get()).addDeco(this.shrubsOak()).addDeco(this.flowers());
   }

   private DecoHelperRandomSplit randomTrees() {
      return (new DecoHelperRandomSplit()).setDecos(new DecoBase[]{this.tallBirchTrees(), this.vanillaTrees()}).setChances(new int[]{10, 4});
   }

   private DecoTree tallBirchTrees() {
      TreeRTG birchTree = (new TreeRTGBetulaPapyrifera()).setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH)).setMinTrunkSize(4).setMaxTrunkSize(10).setMinCrownSize(8).setMaxCrownSize(19);
      this.addTree(birchTree);
      return (new DecoTree(birchTree)).setStrengthFactorForLoops(3.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setTreeCondition(DecoTree.TreeCondition.ALWAYS_GENERATE).setMaxY(100);
   }

   private DecoTree vanillaTrees() {
      return (new DecoTree(new WorldGenTrees(false))).setTreeType(DecoTree.TreeType.WORLDGEN).setStrengthFactorForLoops(3.0F).setTreeCondition(DecoTree.TreeCondition.ALWAYS_GENERATE).setMaxY(100);
   }

   private DecoFallenTree logs() {
      return (new DecoFallenTree()).setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE).setLogConditionChance(8).setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH)).setMinSize(3).setMaxSize(6);
   }

   private DecoShrub shrubsOak() {
      return (new DecoShrub()).setMaxY(120).setLoopMultiplier(3.0F);
   }

   private DecoFlowersRTG flowers() {
      return (new DecoFlowersRTG()).addFlowers(EnumFlowerType.HOUSTONIA, EnumFlowerType.WHITE_TULIP).setMaxY(128).setStrengthFactor(12.0F);
   }
}
