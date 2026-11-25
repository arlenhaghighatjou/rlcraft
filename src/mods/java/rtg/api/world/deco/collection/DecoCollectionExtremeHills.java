package rtg.api.world.deco.collection;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusNigra;

public class DecoCollectionExtremeHills extends DecoCollectionBase {
   public DecoCollectionExtremeHills(BiomeConfig config) {
      super(config);
      this.addDeco(this.nigraDecos(85, 12, 18, 10, 14));
   }

   protected DecoTree nigraTrees(IBlockState log, IBlockState leaves, int maxY, int minTrunkSize, int maxTrunkSize, int minCrownSize, int maxCrownSize) {
      TreeRTG nigraTree = new TreeRTGPinusNigra();
      nigraTree.setLogBlock(log);
      nigraTree.setLeavesBlock(leaves);
      nigraTree.setMinTrunkSize(minTrunkSize);
      nigraTree.setMaxTrunkSize(maxTrunkSize);
      nigraTree.setMinCrownSize(minCrownSize);
      nigraTree.setMaxCrownSize(maxCrownSize);
      this.addTree(nigraTree);
      DecoTree nigraDeco = new DecoTree(nigraTree);
      nigraDeco.setStrengthFactorForLoops(3.0F);
      nigraDeco.setStrengthNoiseFactorXForLoops(true);
      nigraDeco.getDistribution().setNoiseDivisor(100.0F);
      nigraDeco.getDistribution().setNoiseFactor(6.0F);
      nigraDeco.getDistribution().setNoiseAddend(0.8F);
      nigraDeco.setTreeType(DecoTree.TreeType.RTG_TREE);
      nigraDeco.setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE);
      nigraDeco.setTreeConditionChance(20);
      nigraDeco.setMaxY(maxY);
      return nigraDeco;
   }

   protected DecoHelper5050 nigraDecos(int maxY, int minTrunkSize, int maxTrunkSize, int minCrownSize, int maxCrownSize) {
      return new DecoHelper5050(this.nigraTrees(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), maxY, minTrunkSize, maxTrunkSize, minCrownSize, maxCrownSize), this.nigraTrees(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE), maxY, minTrunkSize, maxTrunkSize, minCrownSize, maxCrownSize));
   }
}
