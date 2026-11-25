package vazkii.quark.building.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockFramedGlass extends BlockMod implements IQuarkBlock {
   public BlockFramedGlass() {
      super("framed_glass", Material.GLASS, new String[0]);
      this.setHardness(3.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHarvestLevel("pickaxe", 1);
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }
}
