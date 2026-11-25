package rustic.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockChain extends BlockRopeBase {
   public BlockChain() {
      this("chain");
   }

   public BlockChain(String name) {
      this(Material.IRON, name, true);
   }

   public BlockChain(Material mat, String name, boolean register) {
      super(mat, name, register);
      this.setHardness(1.0F);
      this.setCreativeTab(Rustic.decorTab);
      this.setSoundType(SoundType.METAL);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.Y));
   }

   public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
      if (facing == EnumFacing.DOWN) {
         return false;
      } else {
         IBlockState otherState = world.getBlockState(pos.offset(facing));
         if (otherState.getBlock() != state.getBlock() || (state.getValue(AXIS) != Axis.Y || facing.getAxis() != Axis.Y) && otherState.getValue(AXIS) != state.getValue(AXIS)) {
            if (otherState.getBlock() instanceof BlockLattice) {
               return true;
            } else {
               BlockFaceShape faceShape = otherState.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite());
               return faceShape == BlockFaceShape.SOLID || faceShape == BlockFaceShape.CENTER || faceShape == BlockFaceShape.CENTER_BIG || faceShape == BlockFaceShape.CENTER_SMALL;
            }
         } else {
            return true;
         }
      }
   }

   public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
      BlockPos otherPos = pos.offset(side.getOpposite());
      IBlockState otherState = world.getBlockState(otherPos);
      if (side == EnumFacing.UP) {
         return this.canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
      } else if (otherState.getBlock() == this && otherState.getValue(AXIS) == side.getAxis()) {
         return true;
      } else if (otherState.getBlock() instanceof BlockLattice) {
         return true;
      } else {
         BlockFaceShape faceShape = otherState.getBlockFaceShape(world, otherPos, side);
         return faceShape == BlockFaceShape.SOLID || faceShape == BlockFaceShape.CENTER || faceShape == BlockFaceShape.CENTER_BIG || faceShape == BlockFaceShape.CENTER_SMALL;
      }
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return state.getValue(AXIS) == side.getAxis() ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
   }
}
