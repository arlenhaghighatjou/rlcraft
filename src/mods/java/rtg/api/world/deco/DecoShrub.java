package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenShrubRTG;

public class DecoShrub extends DecoBase {
   private int size;
   private boolean useDefaultRandom;
   private boolean sand;
   private IBlockState[] randomLogBlocks;
   private IBlockState[] randomLeavesBlocks;
   private float loopMultiplier;
   private int minY;
   private int maxY;
   private int chance;
   private int notEqualsZeroChance;
   private int loops;
   private int minSize;
   private int maxSize;
   private IBlockState logBlock;
   private IBlockState leavesBlock;

   public DecoShrub() {
      this.setSize(-1);
      this.setUseDefaultRandom(false);
      this.setSand(false);
      this.setRandomLogBlocks(new IBlockState[]{Blocks.LOG.getDefaultState(), BlockUtil.getStateLog(EnumType.SPRUCE)});
      this.setRandomLeavesBlocks(new IBlockState[]{Blocks.LEAVES.getDefaultState(), BlockUtil.getStateLeaf(EnumType.SPRUCE)});
      this.setLoopMultiplier(1.0F);
      this.setMinY(1);
      this.setMaxY(255);
      this.setChance(1);
      this.setNotEqualsZeroChance(1);
      this.setLoops(1);
      this.setMinSize(3);
      this.setMaxSize(4);
      this.setLogBlock(Blocks.LOG.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.SHRUB});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (!hasVillage && TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.TREE)) {
         this.size = this.size == -1 ? rand.nextInt(4) + 1 : this.size;
         if (this.minSize > 0 && this.maxSize > 0 && this.maxSize >= this.minSize) {
            this.size = this.minSize + rand.nextInt(this.maxSize - this.minSize + 1);
         }

         if (this.useDefaultRandom && this.randomLogBlocks.length > 0 && this.randomLogBlocks.length == this.randomLeavesBlocks.length) {
            int rnd = rand.nextInt(this.randomLogBlocks.length);
            this.setLogBlock(this.randomLogBlocks[rnd]);
            this.setLeavesBlock(this.randomLeavesBlocks[rnd]);
         }

         DecoBase.tweakShrubLeaves(this, false, true);
         int loopCount = (int)((float)this.loops * this.loopMultiplier);

         for(int i = 0; i < loopCount; ++i) {
            BlockPos pos = rtgWorld.world().getHeight(getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16)));
            if (pos.getY() >= this.minY && pos.getY() <= this.maxY) {
               if (this.notEqualsZeroChance > 1) {
                  if (rand.nextInt(this.notEqualsZeroChance) != 0) {
                     (new WorldGenShrubRTG(this.size, this.logBlock, this.leavesBlock, this.sand)).generate(rtgWorld.world(), rand, pos);
                  }
               } else if (rand.nextInt(this.chance) == 0) {
                  (new WorldGenShrubRTG(this.size, this.logBlock, this.leavesBlock, this.sand)).generate(rtgWorld.world(), rand, pos);
               }
            }
         }
      }

   }

   public int getSize() {
      return this.size;
   }

   public DecoShrub setSize(int size) {
      this.size = size;
      return this;
   }

   public boolean isUseDefaultRandom() {
      return this.useDefaultRandom;
   }

   public DecoShrub setUseDefaultRandom(boolean useDefaultRandom) {
      this.useDefaultRandom = useDefaultRandom;
      return this;
   }

   public boolean isSand() {
      return this.sand;
   }

   public DecoShrub setSand(boolean sand) {
      this.sand = sand;
      return this;
   }

   public IBlockState[] getRandomLogBlocks() {
      return this.randomLogBlocks;
   }

   public DecoShrub setRandomLogBlocks(IBlockState[] randomLogBlocks) {
      this.randomLogBlocks = randomLogBlocks;
      return this;
   }

   public IBlockState[] getRandomLeavesBlocks() {
      return this.randomLeavesBlocks;
   }

   public DecoShrub setRandomLeavesBlocks(IBlockState[] randomLeavesBlocks) {
      this.randomLeavesBlocks = randomLeavesBlocks;
      return this;
   }

   public float getLoopMultiplier() {
      return this.loopMultiplier;
   }

   public DecoShrub setLoopMultiplier(float loopMultiplier) {
      this.loopMultiplier = loopMultiplier;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoShrub setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoShrub setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoShrub setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getNotEqualsZeroChance() {
      return this.notEqualsZeroChance;
   }

   public DecoShrub setNotEqualsZeroChance(int notEqualsZeroChance) {
      this.notEqualsZeroChance = notEqualsZeroChance;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoShrub setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   public int getMinSize() {
      return this.minSize;
   }

   public DecoShrub setMinSize(int minSize) {
      this.minSize = minSize;
      return this;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public DecoShrub setMaxSize(int maxSize) {
      this.maxSize = maxSize;
      return this;
   }

   public IBlockState getLogBlock() {
      return this.logBlock;
   }

   public DecoShrub setLogBlock(IBlockState logBlock) {
      this.logBlock = logBlock;
      return this;
   }

   public IBlockState getLeavesBlock() {
      return this.leavesBlock;
   }

   public DecoShrub setLeavesBlock(IBlockState leavesBlock) {
      this.leavesBlock = leavesBlock;
      return this;
   }
}
