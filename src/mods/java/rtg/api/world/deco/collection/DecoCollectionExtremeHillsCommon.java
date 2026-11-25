package rtg.api.world.deco.collection;

import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoMushrooms;
import rtg.api.world.deco.DecoPumpkin;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.helper.DecoHelper5050;

public class DecoCollectionExtremeHillsCommon extends DecoCollectionBase {
   public DecoCollectionExtremeHillsCommon(BiomeConfig config) {
      super(config);
      this.addDeco(this.logDecos(), config.ALLOW_LOGS.get()).addDeco(this.shrubDecos()).addDeco(this.boulders()).addDeco(this.flowers()).addDeco(this.mushrooms()).addDeco(this.pumpkins());
   }

   private DecoFallenTree logs(IBlockState log, IBlockState leaves) {
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(100.0F);
      decoFallenTree.getDistribution().setNoiseFactor(6.0F);
      decoFallenTree.getDistribution().setNoiseAddend(0.8F);
      decoFallenTree.setLogConditionChance(16);
      decoFallenTree.setLogBlock(log);
      decoFallenTree.setLeavesBlock(leaves);
      decoFallenTree.setMinSize(4);
      decoFallenTree.setMaxSize(7);
      decoFallenTree.setMaxY(75);
      return decoFallenTree;
   }

   private DecoHelper5050 logDecos() {
      return new DecoHelper5050(this.logs(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState()), this.logs(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE)));
   }

   private DecoShrub shrubs(IBlockState log, IBlockState leaves) {
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setLogBlock(log);
      decoShrub.setLeavesBlock(leaves);
      decoShrub.setLoopMultiplier(4.0F);
      decoShrub.setChance(2);
      decoShrub.setMaxY(110);
      return decoShrub;
   }

   private DecoHelper5050 shrubDecos() {
      return new DecoHelper5050(this.shrubs(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState()), this.shrubs(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE)));
   }

   private DecoBoulder boulders() {
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.MOSSY_COBBLESTONE.getDefaultState());
      decoBoulder.setChance(12);
      decoBoulder.setMaxY(90);
      decoBoulder.setStrengthFactor(2.0F);
      return decoBoulder;
   }

   private DecoFlowersRTG flowers() {
      DecoFlowersRTG decoFlowersRTG = new DecoFlowersRTG();
      decoFlowersRTG.addFlowers(EnumFlowerType.ALLIUM, EnumFlowerType.HOUSTONIA, EnumFlowerType.DANDELION);
      decoFlowersRTG.setMaxY(80);
      decoFlowersRTG.setLoops(3);
      return decoFlowersRTG;
   }

   private DecoMushrooms mushrooms() {
      DecoMushrooms decoMushrooms = new DecoMushrooms();
      decoMushrooms.setMaxY(70);
      decoMushrooms.setRandomFloat(3.0F);
      return decoMushrooms;
   }

   private DecoPumpkin pumpkins() {
      DecoPumpkin decoPumpkin = new DecoPumpkin();
      decoPumpkin.setMaxY(90);
      decoPumpkin.setRandomFloat(30.0F);
      return decoPumpkin;
   }
}
