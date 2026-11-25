package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenLog;

public class DecoFallenTree extends DecoBase {
   private int loops;
   private DecoFallenTree.Distribution distribution;
   private DecoFallenTree.LogCondition logCondition;
   private float logConditionNoise;
   private int logConditionChance;
   private int maxY;
   private IBlockState logBlock;
   private IBlockState leavesBlock;
   private int minSize;
   private int maxSize;
   private IBlockState[] randomLogBlocks;

   public DecoFallenTree() {
      this.setLoops(1);
      this.setDistribution(new DecoFallenTree.Distribution(100.0F, 5.0F, 0.8F));
      this.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      this.setLogConditionChance(1);
      this.setMaxY(80);
      this.setLogBlock(Blocks.LOG.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      this.setMinSize(2);
      this.setMaxSize(4);
      this.randomLogBlocks = new IBlockState[0];
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.FALLEN_TREE});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      BlockPos offsetpos = getOffsetPos(chunkPos);
      if (this.randomLogBlocks.length > 0) {
         this.setLogBlock(this.randomLogBlocks[rand.nextInt(this.randomLogBlocks.length)]);
      }

      this.setLogConditionChance(this.adjustChanceFromMultiplier(this.getLogConditionChance(), biome.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER.get()));
      int finalSize = this.maxSize > this.minSize ? getRangedRandom(rand, this.minSize, this.maxSize) : (this.maxSize == this.minSize ? this.minSize : 4);

      for(int i = 0; i < this.loops; ++i) {
         if (this.isValidLogCondition(rand)) {
            BlockPos pos = offsetpos.add(rand.nextInt(16), 0, rand.nextInt(16));
            pos = pos.up(rtgWorld.world().getHeight(pos).getY());
            if (pos.getY() <= this.maxY) {
               if (hasVillage && !BlockUtil.checkAreaBlocks(BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE, rtgWorld.world(), pos, finalSize)) {
                  return;
               }

               (new WorldGenLog(this.logBlock, this.leavesBlock, finalSize)).generate(rtgWorld.world(), rand, pos);
            }
         }
      }

   }

   public boolean isValidLogCondition(Random rand) {
      switch(this.logCondition) {
      case ALWAYS_GENERATE:
         return true;
      case RANDOM_CHANCE:
         return rand.nextInt(this.logConditionChance) == 0;
      default:
         return false;
      }
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoFallenTree setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   public DecoFallenTree.Distribution getDistribution() {
      return this.distribution;
   }

   public DecoFallenTree setDistribution(DecoFallenTree.Distribution distribution) {
      this.distribution = distribution;
      return this;
   }

   public DecoFallenTree.LogCondition getLogCondition() {
      return this.logCondition;
   }

   public DecoFallenTree setLogCondition(DecoFallenTree.LogCondition logCondition) {
      this.logCondition = logCondition;
      return this;
   }

   public float getLogConditionNoise() {
      return this.logConditionNoise;
   }

   public int getLogConditionChance() {
      return this.logConditionChance;
   }

   public DecoFallenTree setLogConditionChance(int logConditionChance) {
      this.logConditionChance = logConditionChance;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoFallenTree setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public IBlockState getLogBlock() {
      return this.logBlock;
   }

   public DecoFallenTree setLogBlock(IBlockState logBlock) {
      this.logBlock = logBlock;
      return this;
   }

   public IBlockState getLeavesBlock() {
      return this.leavesBlock;
   }

   public DecoFallenTree setLeavesBlock(IBlockState leavesBlock) {
      this.leavesBlock = leavesBlock;
      return this;
   }

   public int getMinSize() {
      return this.minSize;
   }

   public DecoFallenTree setMinSize(int minSize) {
      this.minSize = minSize;
      return this;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public DecoFallenTree setMaxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
   }

   public IBlockState[] getRandomLogBlocks() {
      return this.randomLogBlocks;
   }

   public DecoFallenTree setRandomLogBlocks(IBlockState[] randomLogBlocks) {
      this.randomLogBlocks = randomLogBlocks;
      return this;
   }

   private int adjustChanceFromMultiplier(int chanceIn, float multiplier) {
      int chanceOut = multiplier != 0.0F ? (int)Math.floor((double)((float)chanceIn / multiplier)) : chanceIn;
      return chanceOut == 0 ? 1 : chanceOut;
   }

   public static class Distribution {
      private float noiseDivisor;
      private float noiseFactor;
      private float noiseAddend;

      public Distribution(float noiseDivisor, float noiseFactor, float noiseAddend) {
         this.noiseDivisor = noiseDivisor;
         this.noiseFactor = noiseFactor;
         this.noiseAddend = noiseAddend;
      }

      public float getNoiseDivisor() {
         return this.noiseDivisor;
      }

      public DecoFallenTree.Distribution setNoiseDivisor(float noiseDivisor) {
         this.noiseDivisor = noiseDivisor;
         return this;
      }

      public float getNoiseFactor() {
         return this.noiseFactor;
      }

      public DecoFallenTree.Distribution setNoiseFactor(float noiseFactor) {
         this.noiseFactor = noiseFactor;
         return this;
      }

      public float getNoiseAddend() {
         return this.noiseAddend;
      }

      public DecoFallenTree.Distribution setNoiseAddend(float noiseAddend) {
         this.noiseAddend = noiseAddend;
         return this;
      }
   }

   public static enum LogCondition {
      ALWAYS_GENERATE,
      RANDOM_CHANCE;

      private LogCondition() {
      }
   }
}
