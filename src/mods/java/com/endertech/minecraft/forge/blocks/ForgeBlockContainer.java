package com.endertech.minecraft.forge.blocks;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForgeBlockContainer<T extends TileEntity> extends ForgeBlock implements ITileEntityProvider {
   private final Class<T> tileEntityClass;

   public ForgeBlockContainer(ForgeMod mod, UnitConfig config, Class<T> tileEntityClass, String name, Material material, CreativeTabs tab) {
      super(mod, config, name, material, tab);
      this.tileEntityClass = tileEntityClass;
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public Class<T> getTileEntityClass() {
      return this.tileEntityClass;
   }

   @Nullable
   public TileEntity getTileEntity(World world, BlockPos pos) {
      return world.getTileEntity(pos);
   }

   @Nullable
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      try {
         return (TileEntity)this.getTileEntityClass().newInstance();
      } catch (IllegalAccessException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
