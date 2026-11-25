package rtg.api.world.deco.collection;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import rtg.api.config.BiomeConfig;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaPungens;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaSitchensis;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusPonderosa;

public class DecoCollectionIceTrees extends DecoCollectionBase {
   private DecoTree.Distribution forestDistribution = new DecoTree.Distribution(100.0F, 6.0F, 0.8F);
   private float short1Min = -3.0F;
   private float short1Max = -1.0F;
   private float tallMin = -1.0F;
   private float tallMax = 3.0F;
   private float short2Min = 3.0F;
   private float short2Max = 5.0F;

   public DecoCollectionIceTrees(BiomeConfig config) {
      super(config);
      this.addDeco(this.tallTrees(this.tallMin, this.tallMax)).addDeco(this.shortTrees(this.short1Min, this.short1Max)).addDeco(this.shortTrees(this.short2Min, this.short2Max)).addDeco(this.randomPungensTrees()).addDeco(this.shrubsIce());
   }

   private DecoHelper5050 tallTrees(float noiseMin, float noiseMax) {
      return new DecoHelper5050(this.tallPineTrees(Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), noiseMin, noiseMax), this.tallPineTrees(Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), noiseMin, noiseMax));
   }

   private DecoTree tallPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
      TreeRTG pinusPonderosa = new TreeRTGPinusPonderosa();
      pinusPonderosa.setLogBlock(log);
      pinusPonderosa.setLeavesBlock(leaves);
      pinusPonderosa.setMinTrunkSize(8);
      pinusPonderosa.setMaxTrunkSize(21);
      pinusPonderosa.setMinCrownSize(13);
      pinusPonderosa.setMaxCrownSize(29);
      ArrayList<IBlockState> validBlocks = pinusPonderosa.getValidGroundBlocks();
      validBlocks.add(Blocks.SNOW.getDefaultState());
      pinusPonderosa.setValidGroundBlocks(validBlocks);
      this.addTree(pinusPonderosa);
      return (new DecoTree(pinusPonderosa)).setStrengthFactorForLoops(4.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(2).setMaxY(85);
   }

   private DecoHelper5050 shortTrees(float noiseMin, float noiseMax) {
      return new DecoHelper5050(this.shortPineTrees(Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), noiseMin, noiseMax), this.shortPineTrees(Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), noiseMin, noiseMax));
   }

   private DecoTree shortPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
      TreeRTG piceaSitchensis = (new TreeRTGPiceaSitchensis()).setLogBlock(log).setLeavesBlock(leaves).setMinTrunkSize(4).setMaxTrunkSize(8).setMinCrownSize(4).setMaxCrownSize(14);
      ArrayList<IBlockState> validBlocks = piceaSitchensis.getValidGroundBlocks();
      validBlocks.add(Blocks.SNOW.getDefaultState());
      piceaSitchensis.setValidGroundBlocks(validBlocks);
      this.addTree(piceaSitchensis);
      return (new DecoTree(piceaSitchensis)).setStrengthFactorForLoops(4.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(2).setMaxY(85);
   }

   private DecoTree randomPungensTrees() {
      TreeRTG piceaPungens = (new TreeRTGPiceaPungens()).setLogBlock(Blocks.PACKED_ICE.getDefaultState()).setLeavesBlock(Blocks.ICE.getDefaultState()).setMinTrunkSize(2).setMaxTrunkSize(4).setMinCrownSize(4).setMaxCrownSize(8);
      ArrayList<IBlockState> validBlocks = piceaPungens.getValidGroundBlocks();
      validBlocks.add(Blocks.SNOW.getDefaultState());
      piceaPungens.setValidGroundBlocks(validBlocks);
      this.addTree(piceaPungens);
      return (new DecoTree(piceaPungens)).setStrengthFactorForLoops(3.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setTreeCondition(DecoTree.TreeCondition.RANDOM_CHANCE).setTreeConditionChance(8).setMaxY(100);
   }

   private DecoShrub shrubsIce() {
      return (new DecoShrub()).setLogBlock(Blocks.PACKED_ICE.getDefaultState()).setLeavesBlock(Blocks.ICE.getDefaultState()).setMaxY(140).setLoopMultiplier(4.0F).setChance(3);
   }
}
