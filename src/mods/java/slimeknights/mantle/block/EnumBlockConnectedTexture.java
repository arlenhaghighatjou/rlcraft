package slimeknights.mantle.block;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class EnumBlockConnectedTexture<E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> extends EnumBlock<E> {
   public EnumBlockConnectedTexture(Material material, PropertyEnum<E> prop, Class<E> clazz) {
      super(material, prop, clazz);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockConnectedTexture.CONNECTED_DOWN, Boolean.FALSE).withProperty(BlockConnectedTexture.CONNECTED_EAST, Boolean.FALSE).withProperty(BlockConnectedTexture.CONNECTED_NORTH, Boolean.FALSE).withProperty(BlockConnectedTexture.CONNECTED_SOUTH, Boolean.FALSE).withProperty(BlockConnectedTexture.CONNECTED_UP, Boolean.FALSE).withProperty(BlockConnectedTexture.CONNECTED_WEST, Boolean.FALSE));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {
      return state.withProperty(BlockConnectedTexture.CONNECTED_DOWN, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.DOWN))).withProperty(BlockConnectedTexture.CONNECTED_EAST, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.EAST))).withProperty(BlockConnectedTexture.CONNECTED_NORTH, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.NORTH))).withProperty(BlockConnectedTexture.CONNECTED_SOUTH, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.SOUTH))).withProperty(BlockConnectedTexture.CONNECTED_UP, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.UP))).withProperty(BlockConnectedTexture.CONNECTED_WEST, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.WEST)));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return this.prop == null ? new BlockStateContainer(this, new IProperty[]{tmp, BlockConnectedTexture.CONNECTED_DOWN, BlockConnectedTexture.CONNECTED_UP, BlockConnectedTexture.CONNECTED_NORTH, BlockConnectedTexture.CONNECTED_SOUTH, BlockConnectedTexture.CONNECTED_WEST, BlockConnectedTexture.CONNECTED_EAST}) : new BlockStateContainer(this, new IProperty[]{this.prop, BlockConnectedTexture.CONNECTED_DOWN, BlockConnectedTexture.CONNECTED_UP, BlockConnectedTexture.CONNECTED_NORTH, BlockConnectedTexture.CONNECTED_SOUTH, BlockConnectedTexture.CONNECTED_WEST, BlockConnectedTexture.CONNECTED_EAST});
   }

   private boolean isSideConnectable(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      IBlockState connected = world.getBlockState(pos.offset(side));
      return connected != null && this.canConnect(state, connected);
   }

   protected boolean canConnect(IBlockState original, IBlockState connected) {
      return connected.getBlock() == original.getBlock() && connected.getValue(this.prop) == original.getValue(this.prop);
   }
}
