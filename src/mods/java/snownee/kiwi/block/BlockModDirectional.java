package snownee.kiwi.block;

import net.minecraft.block.BlockDirectional;
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

public class BlockModDirectional extends BlockMod {
   public BlockModDirectional(String name, Material blockMaterial) {
      super(name, blockMaterial);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.NORTH));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(BlockDirectional.FACING, facing);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.getFront(meta & 7));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(BlockDirectional.FACING)).getIndex();
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(BlockDirectional.FACING, rot.rotate((EnumFacing)state.getValue(BlockDirectional.FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(BlockDirectional.FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockDirectional.FACING});
   }
}
