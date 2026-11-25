package vazkii.quark.automation.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileEnderWatcher;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockEnderWatcher extends BlockModContainer implements IQuarkBlock {
   public static final PropertyBool WATCHED = PropertyBool.create("watched");

   public BlockEnderWatcher() {
      super("ender_watcher", Material.IRON, new String[0]);
      this.setHardness(3.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.METAL);
      this.setDefaultState(this.blockState.getBaseState().withProperty(WATCHED, Boolean.valueOf(false)));
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{WATCHED});
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)blockState.getValue(WATCHED)).booleanValue() ? 15 : 0;
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(WATCHED)).booleanValue() ? 1 : 0;
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(WATCHED, Boolean.valueOf(meta != 0));
   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return new TileEnderWatcher();
   }
}
