package com.creativemd.creativecore.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class IBlockAccessFake implements IBlockAccess {
   public IBlockAccess parent;
   public BlockPos pos;
   public IBlockState fakeState;

   public IBlockAccessFake() {
   }

   public void set(IBlockAccess world, BlockPos pos, IBlockState fakeState) {
      this.parent = world;
      this.pos = pos;
      this.fakeState = fakeState;
   }

   public TileEntity getTileEntity(BlockPos pos) {
      return pos.equals(this.pos) ? null : this.parent.getTileEntity(pos);
   }

   public int getCombinedLight(BlockPos pos, int lightValue) {
      return this.parent.getCombinedLight(pos, lightValue);
   }

   public IBlockState getBlockState(BlockPos pos) {
      return pos.equals(this.pos) ? this.fakeState : this.parent.getBlockState(pos);
   }

   public boolean isAirBlock(BlockPos pos) {
      return pos.equals(this.pos) ? this.fakeState.getBlock().isAir(this.fakeState, this, pos) : this.parent.isAirBlock(pos);
   }

   public Biome getBiome(BlockPos pos) {
      return this.parent.getBiome(pos);
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.parent.getStrongPower(pos, direction);
   }

   public WorldType getWorldType() {
      return this.parent.getWorldType();
   }

   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      return pos.equals(this.pos) ? this.fakeState.isSideSolid(this, pos, side) : this.parent.isSideSolid(pos, side, _default);
   }
}
