package vazkii.quark.experimental.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.experimental.features.FramedBlocks;
import vazkii.quark.experimental.tile.TileFramed;

public class BlockFramed extends BlockModContainer implements IQuarkBlock, ITileEntityProvider {
   public BlockFramed() {
      super("frame", Material.WOOD, new String[0]);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return FramedBlockCommons.createStateContainer(this, super.createBlockState());
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      return FramedBlockCommons.getExtendedState(state, world, pos);
   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return new TileFramed();
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return FramedBlocks.setFrame(worldIn, pos, playerIn, hand);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }
}
