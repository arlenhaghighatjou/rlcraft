package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ModifierBlocksTiles extends ModifierBase {
   private float coldestValue = 0.0F;
   private float hottestValue = 0.0F;
   private float coldestResultValue = 0.0F;
   private float hottestResultValue = 0.0F;
   private float hotTotal = 0.0F;
   private float coldTotal = 0.0F;

   public ModifierBlocksTiles() {
      super("BlocksTiles");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      this.resetHeat();
      this.doBlocksRoutine(world, pos);
      if (ModConfig.server.temperature.blocksTilesSeparate) {
         float result = this.consolidateHeat();
         this.resetHeat();
         this.doTileEntitiesRoutine(world, pos);
         return result + this.consolidateHeat();
      } else {
         this.doTileEntitiesRoutine(world, pos);
         return this.consolidateHeat();
      }
   }

   private void resetHeat() {
      this.coldestValue = 0.0F;
      this.hottestValue = 0.0F;
      this.coldestResultValue = 0.0F;
      this.hottestResultValue = 0.0F;
      this.hotTotal = 0.0F;
      this.coldTotal = 0.0F;
   }

   private float consolidateHeat() {
      if (!ModConfig.server.temperature.stackingTemperature) {
         return this.hottestValue + this.coldestValue;
      } else {
         this.hotTotal -= this.hottestValue;
         this.coldTotal -= this.coldestValue;
         float hotLogValue = this.hottestValue * (float)Math.sqrt((double)this.easyLog(this.hotTotal));
         float coldLogValue = this.coldestValue * (float)Math.sqrt((double)this.easyLog(this.coldTotal));
         float result = hotLogValue + coldLogValue;
         if (result > this.hottestValue) {
            return Math.min(this.hottestValue + (float)ModConfig.server.temperature.stackingTemperatureLimit, result);
         } else {
            return result < this.coldestValue ? Math.max(this.coldestValue - (float)ModConfig.server.temperature.stackingTemperatureLimit, result) : result;
         }
      }
   }

   private void doBlocksRoutine(World world, BlockPos pos) {
      ChunkCache cache = new ChunkCache(world, pos.add(-4, -3, -4), pos.add(4, 1, 4), 0);

      for(int x = -4; x <= 4; ++x) {
         for(int y = -3; y <= 1; ++y) {
            for(int z = -4; z <= 4; ++z) {
               BlockPos blockpos = pos.add(x, y, z);
               IBlockState blockstate = cache.getBlockState(blockpos);
               Block block = blockstate.getBlock();
               List<JsonPropertyTemperature> tempInfoList = (List)JsonConfig.blockTemperatures.get(block.getRegistryName().toString());
               if (tempInfoList != null) {
                  IBlockState actualBlockState = block.getActualState(blockstate, cache, blockpos);

                  for(JsonPropertyTemperature tempInfo : tempInfoList) {
                     if (tempInfo != null) {
                        float blockTemp = tempInfo.temperature;
                        if (blockTemp != 0.0F && tempInfo.matchesState(actualBlockState)) {
                           this.processTemp(blockTemp);
                        }
                     }
                  }
               } else if (blockstate.getMaterial() == Material.FIRE) {
                  this.processTemp(JsonConfigInternal.materialTemperature.fire);
               }
            }
         }
      }

   }

   private void doTileEntitiesRoutine(World world, BlockPos pos) {
      if (ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_TE_ENABLED)) {
         ModifierBlocksTiles.FloatPair pair = new ModifierBlocksTiles.FloatPair();

         for(int x = -3; x <= 3; ++x) {
            for(int z = -3; z <= 3; ++z) {
               this.checkChunkAndProcess(pair, world, pos.add(x * 16, 0, z * 16), pos);
            }
         }

      }
   }

   private void processTemp(float blockTemp) {
      if (blockTemp != 0.0F) {
         if (blockTemp >= 0.0F) {
            this.processHot(blockTemp);
         } else {
            this.processCold(blockTemp);
         }

      }
   }

   private void processHot(float blockTemp) {
      this.hotTotal += blockTemp;
      if (blockTemp > this.hottestValue) {
         this.hottestValue = blockTemp;
      }

   }

   private void processCold(float blockTemp) {
      this.coldTotal += blockTemp;
      if (blockTemp < this.coldestValue) {
         this.coldestValue = blockTemp;
      }

   }

   private void checkChunkAndProcess(ModifierBlocksTiles.FloatPair pair, World world, BlockPos pos, BlockPos selfPos) {
      if (WorldUtil.isChunkLoaded(world, pos)) {
         Chunk chunk = world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);

         for(Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet()) {
            this.processTemp(this.checkTileEntity(world, (BlockPos)entry.getKey(), (TileEntity)entry.getValue(), selfPos));
         }
      }

   }

   private float checkTileEntity(World world, BlockPos pos, TileEntity tileEntity, BlockPos selfPos) {
      double distance = pos.distanceSq(selfPos);
      return distance < 2500.0D && tileEntity instanceof ITemperatureTileEntity ? ((ITemperatureTileEntity)tileEntity).getInfluence(selfPos, distance) : 0.0F;
   }

   private float easyLog(float f) {
      return f >= 0.0F ? (float)Math.log10((double)(f + 10.0F)) : (float)Math.log10((double)(-1.0F * f + 10.0F));
   }

   private float easyLogPowTwo(float f) {
      float ff = this.easyLog(f);
      return ff * ff;
   }

   private float easyLogPowEight(float f) {
      float ff = this.easyLog(f);
      ff = ff * ff;
      ff = ff * ff;
      return ff * ff;
   }

   private class FloatPair {
      protected float high = 0.0F;
      protected float low = 0.0F;

      protected FloatPair() {
      }
   }
}
