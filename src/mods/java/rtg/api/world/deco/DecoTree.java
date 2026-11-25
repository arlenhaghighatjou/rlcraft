package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import rtg.RTGConfig;
import rtg.api.event.DecorateBiomeEventRTG;
import rtg.api.util.BlockUtil;
import rtg.api.util.Logger;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;

public class DecoTree extends DecoBase {
   public static final double MAX_TREE_DENSITY = 5.0D;
   protected int loops;
   protected float strengthFactorForLoops;
   protected boolean strengthNoiseFactorForLoops;
   protected boolean strengthNoiseFactorXForLoops;
   protected DecoTree.TreeType treeType;
   protected TreeRTG tree;
   protected WorldGenerator worldGen;
   protected DecoTree.Distribution distribution;
   protected DecoTree.TreeCondition treeCondition;
   protected float treeConditionNoise;
   protected float treeConditionNoise2;
   protected int treeConditionChance;
   protected float treeConditionFloat;
   protected int minY;
   protected int maxY;
   protected IBlockState logBlock;
   protected IBlockState leavesBlock;
   protected int minSize;
   protected int maxSize;
   protected int minTrunkSize;
   protected int maxTrunkSize;
   protected int minCrownSize;
   protected int maxCrownSize;
   protected boolean noLeaves;

   public DecoTree() {
      this.setLoops(1);
      this.setStrengthFactorForLoops(0.0F);
      this.setStrengthNoiseFactorForLoops(false);
      this.setStrengthNoiseFactorXForLoops(false);
      this.setTreeType(DecoTree.TreeType.RTG_TREE);
      this.tree = null;
      this.worldGen = null;
      this.setDistribution(new DecoTree.Distribution(100.0F, 5.0F, 0.8F));
      this.setTreeCondition(DecoTree.TreeCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
      this.setTreeConditionNoise(0.0F);
      this.setTreeConditionNoise2(0.0F);
      this.setTreeConditionFloat(0.0F);
      this.setTreeConditionChance(1);
      this.setMinY(63);
      this.setMaxY(230);
      this.setLogBlock(Blocks.LOG.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      this.setMinSize(2);
      this.setMaxSize(4);
      this.setMinTrunkSize(2);
      this.setMaxTrunkSize(4);
      this.setMinCrownSize(2);
      this.setMaxCrownSize(4);
      this.setNoLeaves(false);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.TREE});
   }

   public DecoTree(DecoTree source) {
      this();
      this.setLoops(source.loops);
      this.setStrengthFactorForLoops(source.strengthFactorForLoops);
      this.setStrengthNoiseFactorForLoops(source.strengthNoiseFactorForLoops);
      this.setStrengthNoiseFactorXForLoops(source.strengthNoiseFactorXForLoops);
      this.setTreeType(source.treeType);
      this.tree = source.tree;
      this.worldGen = source.worldGen;
      this.setDistribution(source.distribution);
      this.setTreeCondition(source.treeCondition);
      this.setTreeConditionNoise(source.treeConditionNoise);
      this.setTreeConditionNoise2(source.treeConditionNoise2);
      this.setTreeConditionFloat(source.treeConditionFloat);
      this.setTreeConditionChance(source.treeConditionChance);
      this.setMinY(source.minY);
      this.setMaxY(source.maxY);
      this.setLogBlock(source.logBlock);
      this.setLeavesBlock(source.leavesBlock);
      this.setMinSize(source.minSize);
      this.setMaxSize(source.maxSize);
      this.setMinTrunkSize(source.minTrunkSize);
      this.setMaxTrunkSize(source.maxTrunkSize);
      this.setMinCrownSize(source.minCrownSize);
      this.setMaxCrownSize(source.maxCrownSize);
      this.setNoLeaves(source.noLeaves);
   }

   public DecoTree(TreeRTG tree) {
      this();
      this.tree = tree;
      this.setLogBlock(tree.getLogBlock());
      this.setLeavesBlock(tree.getLeavesBlock());
      this.setMinTrunkSize(tree.getMinTrunkSize());
      this.setMaxTrunkSize(tree.getMaxTrunkSize());
      this.setMinCrownSize(tree.getMinCrownSize());
      this.setMaxCrownSize(tree.getMaxCrownSize());
      this.setNoLeaves(tree.getNoLeaves());
   }

   public DecoTree(WorldGenerator worldGen) {
      this();
      this.worldGen = worldGen;
   }

   /** @deprecated */
   @Deprecated
   public boolean properlyDefined() {
      return this.treeType == DecoTree.TreeType.RTG_TREE && this.tree == null ? false : super.properlyDefined();
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      BlockPos offsetPos = getOffsetPos(chunkPos);
      float noise = rtgWorld.simplexInstance(0).noise2f((float)offsetPos.getX() / this.distribution.getNoiseDivisor(), (float)offsetPos.getZ() / this.distribution.getNoiseDivisor()) * this.distribution.getNoiseFactor() + this.distribution.getNoiseAddend();
      int loopCount = this.strengthFactorForLoops > 0.0F ? (int)this.strengthFactorForLoops : this.loops;
      loopCount = this.strengthNoiseFactorForLoops ? (int)noise : loopCount;
      loopCount = this.strengthNoiseFactorXForLoops ? (int)(noise * this.strengthFactorForLoops) : loopCount;
      if (loopCount >= 1) {
         loopCount = this.calculateLoopCountFromTreeDensity(loopCount, biome);
         if (loopCount >= 1) {
            DecorateBiomeEventRTG.DecorateRTG event = new DecorateBiomeEventRTG.DecorateRTG(rtgWorld.world(), rand, offsetPos, EventType.TREE, loopCount);
            MinecraftForge.TERRAIN_GEN_BUS.post(event);
            if (event.getResult() != Result.DENY) {
               loopCount = event.getModifiedAmount();
               if (loopCount < 1) {
                  return;
               }

               DecoBase.tweakTreeLeaves(this, false, true);

               for(int i = 0; i < loopCount; ++i) {
                  BlockPos pos = offsetPos.add(rand.nextInt(16), 0, rand.nextInt(16));
                  int y = rtgWorld.world().getHeight(pos).getY();
                  if (y <= this.maxY && y >= this.minY && this.isValidTreeCondition(noise, rand)) {
                     if (hasVillage && (BlockUtil.checkVerticalBlocks(BlockUtil.MatchType.ALL, rtgWorld.world(), pos, -1, Blocks.FARMLAND) || !BlockUtil.checkAreaBlocks(BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE, rtgWorld.world(), pos, 2))) {
                        return;
                     }

                     switch(this.treeType) {
                     case RTG_TREE:
                        this.tree.setLogBlock(this.logBlock);
                        this.tree.setLeavesBlock(this.leavesBlock);
                        this.tree.setTrunkSize(getRangedRandom(rand, this.minTrunkSize, this.maxTrunkSize));
                        this.tree.setCrownSize(getRangedRandom(rand, this.minCrownSize, this.maxCrownSize));
                        this.tree.setNoLeaves(this.noLeaves);
                        this.tree.generate(rtgWorld.world(), rand, pos.up(y));
                        break;
                     case WORLDGEN:
                        WorldGenerator worldgenerator = this.worldGen;
                        worldgenerator.generate(rtgWorld.world(), rand, pos.up(y));
                     }
                  }
               }
            } else if (RTGConfig.enableDebugging()) {
               Logger.debug("Tree generation was cancelled @ ChunkPos{}", chunkPos);
            }

         }
      }
   }

   public boolean isValidTreeCondition(float noise, Random rand) {
      switch(this.treeCondition) {
      case ALWAYS_GENERATE:
         return true;
      case NOISE_GREATER_AND_RANDOM_CHANCE:
         return noise > this.treeConditionNoise && rand.nextInt(this.treeConditionChance) == 0;
      case NOISE_LESSER_AND_RANDOM_CHANCE:
         return noise < this.treeConditionNoise && rand.nextInt(this.treeConditionChance) == 0;
      case NOISE_BETWEEN_AND_RANDOM_CHANCE:
         boolean noiseGreaterThanMin = noise >= this.treeConditionNoise;
         boolean noiseLessThanMax = -noise <= this.treeConditionNoise2;
         boolean randomResult = rand.nextInt(this.treeConditionChance) == 0;
         return noiseGreaterThanMin && noiseLessThanMax && randomResult;
      case RANDOM_CHANCE:
         return rand.nextInt(this.treeConditionChance) == 0;
      case RANDOM_NOT_EQUALS_CHANCE:
         return rand.nextInt(this.treeConditionChance) != 0;
      default:
         return false;
      }
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoTree setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   public float getStrengthFactorForLoops() {
      return this.strengthFactorForLoops;
   }

   public DecoTree setStrengthFactorForLoops(float strengthFactorForLoops) {
      this.strengthFactorForLoops = strengthFactorForLoops;
      return this;
   }

   public boolean isStrengthNoiseFactorForLoops() {
      return this.strengthNoiseFactorForLoops;
   }

   public DecoTree setStrengthNoiseFactorForLoops(boolean strengthNoiseFactorForLoops) {
      this.strengthNoiseFactorForLoops = strengthNoiseFactorForLoops;
      return this;
   }

   public boolean isStrengthNoiseFactorXForLoops() {
      return this.strengthNoiseFactorXForLoops;
   }

   public DecoTree setStrengthNoiseFactorXForLoops(boolean strengthNoiseFactorXForLoops) {
      this.strengthNoiseFactorXForLoops = strengthNoiseFactorXForLoops;
      return this;
   }

   public DecoTree.TreeType getTreeType() {
      return this.treeType;
   }

   public DecoTree setTreeType(DecoTree.TreeType treeType) {
      this.treeType = treeType;
      return this;
   }

   public TreeRTG getTree() {
      return this.tree;
   }

   public DecoTree setTree(TreeRTG tree) {
      this.tree = tree;
      return this;
   }

   public WorldGenerator getWorldGen() {
      return this.worldGen;
   }

   public DecoTree setWorldGen(WorldGenerator worldGen) {
      this.worldGen = worldGen;
      return this;
   }

   public DecoTree.Distribution getDistribution() {
      return this.distribution;
   }

   public DecoTree setDistribution(DecoTree.Distribution distribution) {
      this.distribution = distribution;
      return this;
   }

   public DecoTree.TreeCondition getTreeCondition() {
      return this.treeCondition;
   }

   public DecoTree setTreeCondition(DecoTree.TreeCondition treeCondition) {
      this.treeCondition = treeCondition;
      return this;
   }

   public float getTreeConditionNoise() {
      return this.treeConditionNoise;
   }

   public DecoTree setTreeConditionNoise(float treeConditionNoise) {
      this.treeConditionNoise = treeConditionNoise;
      return this;
   }

   public float getTreeConditionNoise2() {
      return this.treeConditionNoise2;
   }

   public DecoTree setTreeConditionNoise2(float treeConditionNoise2) {
      this.treeConditionNoise2 = treeConditionNoise2;
      return this;
   }

   public int getTreeConditionChance() {
      return this.treeConditionChance;
   }

   public DecoTree setTreeConditionChance(int treeConditionChance) {
      this.treeConditionChance = treeConditionChance;
      return this;
   }

   public float getTreeConditionFloat() {
      return this.treeConditionFloat;
   }

   public DecoTree setTreeConditionFloat(float treeConditionFloat) {
      this.treeConditionFloat = treeConditionFloat;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoTree setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoTree setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public IBlockState getLogBlock() {
      return this.logBlock;
   }

   public DecoTree setLogBlock(IBlockState logBlock) {
      this.logBlock = logBlock;
      return this;
   }

   public IBlockState getLeavesBlock() {
      return this.leavesBlock;
   }

   public DecoTree setLeavesBlock(IBlockState leavesBlock) {
      this.leavesBlock = leavesBlock;
      return this;
   }

   public int getMinSize() {
      return this.minSize;
   }

   public DecoTree setMinSize(int minSize) {
      this.minSize = minSize;
      return this;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public DecoTree setMaxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
   }

   public int getMinTrunkSize() {
      return this.minTrunkSize;
   }

   public DecoTree setMinTrunkSize(int minTrunkSize) {
      this.minTrunkSize = minTrunkSize;
      return this;
   }

   public int getMaxTrunkSize() {
      return this.maxTrunkSize;
   }

   public DecoTree setMaxTrunkSize(int maxTrunkSize) {
      this.maxTrunkSize = maxTrunkSize;
      return this;
   }

   public int getMinCrownSize() {
      return this.minCrownSize;
   }

   public DecoTree setMinCrownSize(int minCrownSize) {
      this.minCrownSize = minCrownSize;
      return this;
   }

   public int getMaxCrownSize() {
      return this.maxCrownSize;
   }

   public DecoTree setMaxCrownSize(int maxCrownSize) {
      this.maxCrownSize = maxCrownSize;
      return this;
   }

   public boolean isNoLeaves() {
      return this.noLeaves;
   }

   public DecoTree setNoLeaves(boolean noLeaves) {
      this.noLeaves = noLeaves;
      return this;
   }

   private int calculateLoopCountFromTreeDensity(int loopCount, IRealisticBiome biome) {
      double multiplier = RTGConfig.treeDensityMultiplier();
      float biomeMultiplier;
      if ((biomeMultiplier = biome.getConfig().TREE_DENSITY_MULTIPLIER.get()) >= 0.0F) {
         multiplier = (double)biomeMultiplier > 5.0D ? 5.0D : (double)biomeMultiplier;
      }

      loopCount = (int)((double)loopCount * multiplier);
      return loopCount;
   }

   /** @deprecated */
   @Deprecated
   public static class Distribution {
      protected float noiseDivisor;
      protected float noiseFactor;
      protected float noiseAddend;

      public Distribution(float noiseDivisor, float noiseFactor, float noiseAddend) {
         this.noiseDivisor = noiseDivisor;
         this.noiseFactor = noiseFactor;
         this.noiseAddend = noiseAddend;
      }

      public float getNoiseDivisor() {
         return this.noiseDivisor;
      }

      public DecoTree.Distribution setNoiseDivisor(float noiseDivisor) {
         this.noiseDivisor = noiseDivisor;
         return this;
      }

      public float getNoiseFactor() {
         return this.noiseFactor;
      }

      public DecoTree.Distribution setNoiseFactor(float noiseFactor) {
         this.noiseFactor = noiseFactor;
         return this;
      }

      public float getNoiseAddend() {
         return this.noiseAddend;
      }

      public DecoTree.Distribution setNoiseAddend(float noiseAddend) {
         this.noiseAddend = noiseAddend;
         return this;
      }
   }

   public static enum TreeCondition {
      ALWAYS_GENERATE,
      NOISE_GREATER_AND_RANDOM_CHANCE,
      NOISE_LESSER_AND_RANDOM_CHANCE,
      NOISE_BETWEEN_AND_RANDOM_CHANCE,
      RANDOM_CHANCE,
      RANDOM_NOT_EQUALS_CHANCE;

      private TreeCondition() {
      }
   }

   public static enum TreeType {
      RTG_TREE,
      WORLDGEN;

      private TreeType() {
      }
   }
}
