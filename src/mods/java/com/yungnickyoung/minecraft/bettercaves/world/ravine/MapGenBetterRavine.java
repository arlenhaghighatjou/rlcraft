package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.WaterRegionController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class MapGenBetterRavine extends MapGenRavine {
   private ConfigHolder config;
   private WaterRegionController waterRegionController;
   private MapGenBase defaultRavineGen;
   IBlockState[][] currChunkLiquidBlocks;
   int currChunkX;
   int currChunkZ;

   public MapGenBetterRavine(InitMapGenEvent event) {
      this.defaultRavineGen = event.getOriginalGen();
   }

   public void generate(World worldIn, int x, int z, @Nonnull ChunkPrimer primer) {
      if (!BetterCavesUtils.isDimensionWhitelisted(worldIn.provider.getDimension())) {
         this.defaultRavineGen.generate(worldIn, x, z, primer);
      } else {
         if (this.config == null) {
            this.initialize(worldIn);
         }

         if (((Boolean)this.config.enableVanillaRavines.get()).booleanValue()) {
            super.generate(worldIn, x, z, primer);
         }

      }
   }

   protected void digBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
      BlockPos pos = new BlockPos(x + chunkX * 16, y, z + chunkZ * 16);
      IBlockState liquidBlockState;
      if (this.currChunkLiquidBlocks != null && chunkX == this.currChunkX && chunkZ == this.currChunkZ) {
         try {
            liquidBlockState = this.currChunkLiquidBlocks[BetterCavesUtils.getLocal(x)][BetterCavesUtils.getLocal(z)];
         } catch (Exception var12) {
            liquidBlockState = Blocks.LAVA.getDefaultState();
         }
      } else {
         try {
            this.currChunkLiquidBlocks = this.waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);
            liquidBlockState = this.currChunkLiquidBlocks[BetterCavesUtils.getLocal(x)][BetterCavesUtils.getLocal(z)];
            this.currChunkX = chunkX;
            this.currChunkZ = chunkZ;
         } catch (Exception var13) {
            liquidBlockState = Blocks.LAVA.getDefaultState();
         }
      }

      boolean flooded = ((Boolean)this.config.enableFloodedRavines.get()).booleanValue() && BiomeDictionary.hasType(this.world.getBiome(pos), Type.OCEAN) && y < this.world.getSeaLevel();
      if (flooded) {
         float smoothAmpFactor = BetterCavesUtils.biomeDistanceFactor(this.world, pos, 2, (b) -> {
            return !BiomeDictionary.hasType(b, Type.OCEAN);
         });
         if (smoothAmpFactor <= 0.25F) {
            return;
         }
      }

      IBlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : AIR;
      CarverUtils.digBlock(this.world, primer, pos, airBlockState, liquidBlockState, ((Integer)this.config.liquidAltitude.get()).intValue(), ((Boolean)this.config.replaceFloatingGravel.get()).booleanValue());
   }

   protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
      return false;
   }

   private void initialize(World worldIn) {
      this.world = worldIn;
      int dimensionID = worldIn.provider.getDimension();
      this.config = ConfigLoader.loadConfigFromFileForDimension(dimensionID);
      this.waterRegionController = new WaterRegionController(this.world, this.config);
   }
}
