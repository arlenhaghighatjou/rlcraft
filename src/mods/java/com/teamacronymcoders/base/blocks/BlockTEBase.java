package com.teamacronymcoders.base.blocks;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTEBase<T extends TileEntity> extends BlockBase implements IHasTileEntity {
   public BlockTEBase(Material material, String name) {
      super(material, name);
   }

   public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      super.breakBlock(world, pos, state);
      world.removeTileEntity(pos);
   }

   public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
      super.eventReceived(state, world, pos, id, param);
      TileEntity tileentity = world.getTileEntity(pos);
      return tileentity != null && tileentity.receiveClientEvent(id, param);
   }

   public Optional<T> getTileEntity(IBlockAccess world, BlockPos pos) {
      TileEntity tileEntity = world.getTileEntity(pos);
      T actualTileEntity = null;
      if (tileEntity != null && tileEntity.getClass() == this.getTileEntityClass()) {
         actualTileEntity = tileEntity;
      }

      return Optional.ofNullable(actualTileEntity);
   }

   public boolean hasTileEntity(IBlockState blockState) {
      return true;
   }

   @Nonnull
   public abstract TileEntity createTileEntity(@Nonnull World var1, @Nonnull IBlockState var2);
}
