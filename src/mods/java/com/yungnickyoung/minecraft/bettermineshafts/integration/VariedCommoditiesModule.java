package com.yungnickyoung.minecraft.bettermineshafts.integration;

import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.VCBlocks;
import noppes.vc.blocks.tiles.TileCandle;
import noppes.vc.blocks.tiles.TileLamp;

public class VariedCommoditiesModule extends CompatModule {
   public VariedCommoditiesModule() {
      super("variedcommodities");
   }

   public void enable() {
      super.enable();
      if (VCBlocks.lamp != null) {
         this.addIfAbsent(this.lanterns, VCBlocks.lamp.getDefaultState());
      }

      if (VCBlocks.candle != null) {
         this.addIfAbsent(this.lanterns, VCBlocks.candle.getDefaultState());
         this.addIfAbsent(this.leftTorches, VCBlocks.candle.getDefaultState());
         this.addIfAbsent(this.rightTorches, VCBlocks.candle.getDefaultState());
      }

   }

   public boolean shouldBeEnabled() {
      return super.shouldBeEnabled() && Configuration.modCompat.variedcommoditiesCandlesEnabled;
   }

   public boolean isTorchVariedCommoditiesCandle(IBlockState torchBlock) {
      return this.isEnabled() && torchBlock == VCBlocks.candle.getDefaultState();
   }

   public boolean isLanternVariedCommoditiesCandle(IBlockState lanternBlock) {
      return this.isEnabled() && lanternBlock == VCBlocks.candle.getDefaultState();
   }

   public boolean isLanternVariedCommoditiesLamp(IBlockState lanternBlock) {
      return this.isEnabled() && lanternBlock == VCBlocks.lamp.getDefaultState();
   }

   public void spawnCandleTorchEntity(World world, int x, int y, int z, int rotation) {
      this.spawnCandleEntity(world, x, y, z, rotation, 2);
   }

   public void spawnCandleLanternEntity(World world, int x, int y, int z) {
      int rotation = x % 2 == 0 ? 6 : 3;
      this.spawnCandleEntity(world, x, y, z, rotation, 1);
   }

   public void spawnLampLanternEntity(World world, int x, int y, int z) {
      int rotation = x % 2 == 0 ? 5 : 2;
      TileEntity lampEntity = this.tryToGetEntity(world, x, y, z);
      if (lampEntity instanceof TileLamp) {
         ((TileLamp)lampEntity).rotation = rotation;
         ((TileLamp)lampEntity).color = 1;
      }

   }

   private void spawnCandleEntity(World world, int x, int y, int z, int rotation, int color) {
      TileEntity candleEntity = this.tryToGetEntity(world, x, y, z);
      if (candleEntity instanceof TileCandle) {
         ((TileCandle)candleEntity).rotation = rotation;
         ((TileCandle)candleEntity).color = color;
      }

   }

   private TileEntity tryToGetEntity(World world, int x, int y, int z) {
      TileEntity candleEntity = world.getTileEntity(new BlockPos((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D));
      if (candleEntity == null) {
         candleEntity = world.getTileEntity(new BlockPos((double)x - 0.5D, (double)y + 0.5D, (double)z - 0.5D));
      }

      if (candleEntity == null) {
         candleEntity = world.getTileEntity(new BlockPos((double)x + 0.5D, (double)y + 0.5D, (double)z - 0.5D));
      }

      if (candleEntity == null) {
         candleEntity = world.getTileEntity(new BlockPos((double)x - 0.5D, (double)y + 0.5D, (double)z + 0.5D));
      }

      if (candleEntity == null) {
         candleEntity = world.getTileEntity(new BlockPos((double)x, (double)y + 0.5D, (double)z));
      }

      return candleEntity;
   }
}
