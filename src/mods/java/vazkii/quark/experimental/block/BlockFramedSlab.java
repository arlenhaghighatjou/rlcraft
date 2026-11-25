package vazkii.quark.experimental.block;

import javax.annotation.Nonnull;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModSlab;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.experimental.features.FramedBlocks;
import vazkii.quark.experimental.tile.TileFramed;

public class BlockFramedSlab extends BlockModSlab implements IQuarkBlock, ITileEntityProvider {
   public BlockFramedSlab(boolean doubleSlab) {
      super("frame_slab", Material.WOOD, doubleSlab);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.hasTileEntity = true;
   }

   public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   @Nonnull
   public BlockStateContainer createBlockState() {
      return FramedBlockCommons.createStateContainer(this, super.createBlockState());
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      return FramedBlockCommons.getExtendedState(state, world, pos);
   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return new TileFramed();
   }

   @Nonnull
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return FramedBlocks.setFrame(worldIn, pos, playerIn, hand);
   }
}
