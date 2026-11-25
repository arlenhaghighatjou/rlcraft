package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterRegionController {
   private FastNoise waterRegionController;
   private long worldSeed;
   private int dimensionID;
   private String dimensionName;
   private Random rand;
   private IBlockState lavaBlock;
   private IBlockState waterBlock;
   private float waterRegionThreshold;
   private static final float SMOOTH_RANGE = 0.04F;
   private static final float SMOOTH_DELTA = 0.01F;

   public WaterRegionController(World world, ConfigHolder config) {
      this.worldSeed = world.getSeed();
      this.dimensionID = world.provider.getDimension();
      this.dimensionName = world.provider.getDimensionType().toString();
      this.rand = new Random();
      this.lavaBlock = this.getLavaBlockFromString(config.lavaBlock.get());
      this.waterBlock = this.getWaterBlockFromString(config.waterBlock.get());
      this.waterRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1.0F, ((Float)config.waterRegionSpawnChance.get()).floatValue() / 100.0F);
      float waterRegionSize = this.calcWaterRegionSize(config.waterRegionSize.get(), ((Float)config.waterRegionCustomSize.get()).floatValue());
      this.waterRegionController = new FastNoise();
      this.waterRegionController.SetSeed((int)world.getSeed() + 444);
      this.waterRegionController.SetFrequency(waterRegionSize);
   }

   public IBlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
      this.rand.setSeed(this.worldSeed ^ (long)chunkX ^ (long)chunkZ);
      IBlockState[][] blocks = new IBlockState[16][16];

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int realX = chunkX * 16 + x;
            int realZ = chunkZ * 16 + z;
            BlockPos pos = new BlockPos(realX, 1, realZ);
            blocks[x][z] = this.getLiquidBlockAtPos(this.rand, pos);
         }
      }

      return blocks;
   }

   private IBlockState getLiquidBlockAtPos(Random rand, BlockPos blockPos) {
      IBlockState liquidBlock = this.lavaBlock;
      if (this.waterRegionThreshold > -1.0F) {
         float waterRegionNoise = this.waterRegionController.GetNoise((float)blockPos.getX(), (float)blockPos.getZ());
         float randOffset = rand.nextFloat() * 0.01F + 0.04F;
         if (waterRegionNoise < this.waterRegionThreshold - randOffset) {
            liquidBlock = this.waterBlock;
         } else if (waterRegionNoise < this.waterRegionThreshold + randOffset) {
            liquidBlock = null;
         }
      }

      return liquidBlock;
   }

   private IBlockState getLavaBlockFromString(String lavaString) {
      IBlockState lavaBlock;
      try {
         lavaBlock = Block.getBlockFromName(lavaString).getDefaultState();
         BetterCaves.LOGGER.info("Using block '" + lavaString + "' as lava in cave generation for dimension " + BetterCavesUtils.dimensionAsString(this.dimensionID, this.dimensionName) + " ...");
      } catch (Exception var4) {
         BetterCaves.LOGGER.warn("Unable to use block '" + lavaString + "': " + var4);
         BetterCaves.LOGGER.warn("Using vanilla lava instead...");
         lavaBlock = Blocks.LAVA.getDefaultState();
      }

      if (lavaBlock == null) {
         BetterCaves.LOGGER.warn("Unable to use block '" + lavaString + "': null block returned.\n Using vanilla lava instead...");
         lavaBlock = Blocks.LAVA.getDefaultState();
      }

      return lavaBlock;
   }

   private IBlockState getWaterBlockFromString(String waterString) {
      IBlockState waterBlock;
      try {
         waterBlock = Block.getBlockFromName(waterString).getDefaultState();
         BetterCaves.LOGGER.info("Using block '" + waterString + "' as water in cave generation for dimension " + BetterCavesUtils.dimensionAsString(this.dimensionID, this.dimensionName) + " ...");
      } catch (Exception var4) {
         BetterCaves.LOGGER.warn("Unable to use block '" + waterString + "': " + var4);
         BetterCaves.LOGGER.warn("Using vanilla water instead...");
         waterBlock = Blocks.WATER.getDefaultState();
      }

      if (waterBlock == null) {
         BetterCaves.LOGGER.warn("Unable to use block '" + waterString + "': null block returned.\n Using vanilla water instead...");
         waterBlock = Blocks.WATER.getDefaultState();
      }

      return waterBlock;
   }

   private float calcWaterRegionSize(RegionSize waterRegionSize, float waterRegionCustomSize) {
      switch(waterRegionSize) {
      case Small:
         return 0.008F;
      case Large:
         return 0.0028F;
      case ExtraLarge:
         return 0.001F;
      case Custom:
         return waterRegionCustomSize;
      default:
         return 0.004F;
      }
   }
}
