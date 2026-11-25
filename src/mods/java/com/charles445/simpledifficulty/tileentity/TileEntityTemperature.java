package com.charles445.simpledifficulty.tileentity;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.block.BlockTemperature;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class TileEntityTemperature extends TileEntity implements ITemperatureTileEntity {
   public TileEntityTemperature() {
   }

   public float getInfluence(BlockPos targetPos, double distance) {
      IBlockState state = this.world.getBlockState(this.pos);
      Block block = state.getBlock();
      if (block instanceof BlockTemperature) {
         boolean enabled = ((Boolean)this.world.getBlockState(this.pos).getValue(BlockTemperature.ENABLED)).booleanValue();
         if (enabled) {
            float activeTemp = ((BlockTemperature)block).getActiveTemperatureMult() * (float)ModConfig.server.temperature.heaterTemperature;
            double fullPowerSq = this.sq(ModConfig.server.temperature.heaterFullPowerRange);
            if (distance < fullPowerSq) {
               return this.handleStrict(targetPos, activeTemp);
            } else {
               double distanceDiv = this.sq(ModConfig.server.temperature.heaterMaxRange) - fullPowerSq;
               return distanceDiv <= 0.0D ? 0.0F : this.handleStrict(targetPos, activeTemp * Math.max(0.0F, 1.0F - (float)((distance - fullPowerSq) / distanceDiv)));
            }
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private float handleStrict(BlockPos targetPos, float distanceTemp) {
      if (!ServerConfig.instance.getBoolean(ServerOptions.STRICT_HEATERS)) {
         return distanceTemp;
      } else {
         BlockPos thisPos = this.getPos();
         int curX = targetPos.getX();
         int curY = targetPos.getY();
         int curZ = targetPos.getZ();
         int destX = thisPos.getX();
         int destY = thisPos.getY();
         int destZ = thisPos.getZ();
         int xinc = curX < destX ? 1 : -1;
         int yinc = curY < destY ? 1 : -1;
         int zinc = curZ < destZ ? 1 : -1;
         if (!this.isUnprotected(new BlockPos(curX, curY, curZ)) && !this.isUnprotected(new BlockPos(destX, destY, destZ))) {
            while(curX != destX || curZ != destZ || curY != destY) {
               if (curX != destX) {
                  curX += xinc;
               }

               if (curY != destY) {
                  curY += yinc;
               }

               if (curZ != destZ) {
                  curZ += zinc;
               }

               if (this.isUnprotected(new BlockPos(curX, curY, curZ))) {
                  return 0.0F;
               }
            }

            return distanceTemp;
         } else {
            return 0.0F;
         }
      }
   }

   private boolean isUnprotected(BlockPos pos) {
      if (!WorldUtil.isChunkLoaded(this.world, pos)) {
         return true;
      } else {
         Chunk chunk = this.world.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
         if (!chunk.canSeeSky(pos)) {
            return false;
         } else {
            return chunk.getPrecipitationHeight(pos).getY() <= pos.getY();
         }
      }
   }

   private double sq(double d) {
      return d * d;
   }
}
