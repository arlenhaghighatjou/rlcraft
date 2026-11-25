package vazkii.quark.automation.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileChute;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockChute extends BlockModContainer implements IQuarkBlock {
   public static final PropertyBool ENABLED = PropertyBool.create("enabled");

   public BlockChute() {
      super("chute", Material.WOOD, new String[0]);
      this.setHardness(2.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setDefaultState(this.getDefaultState().withProperty(ENABLED, Boolean.valueOf(true)));
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean flag = !worldIn.isBlockPowered(pos);
      if (flag != ((Boolean)state.getValue(ENABLED)).booleanValue()) {
         worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 6);
      }

   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return new TileChute();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{ENABLED});
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{ENABLED};
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(ENABLED)).booleanValue() ? 0 : 1;
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(ENABLED, Boolean.valueOf((meta & 1) != 1));
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean isTopSolid(IBlockState state) {
      return true;
   }
}
