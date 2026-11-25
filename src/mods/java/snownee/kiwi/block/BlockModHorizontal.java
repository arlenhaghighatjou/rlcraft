package snownee.kiwi.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockModHorizontal extends BlockMod {
   public BlockModHorizontal(String name, Material materialIn) {
      super(name, materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta & 3));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(BlockHorizontal.FACING)).getHorizontalIndex();
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(BlockHorizontal.FACING, rot.rotate((EnumFacing)state.getValue(BlockHorizontal.FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(BlockHorizontal.FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockHorizontal.FACING});
   }
}
