package slimeknights.mantle.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockConnectedTexture extends Block {
   public static final PropertyBool CONNECTED_DOWN = PropertyBool.create("connected_down");
   public static final PropertyBool CONNECTED_UP = PropertyBool.create("connected_up");
   public static final PropertyBool CONNECTED_NORTH = PropertyBool.create("connected_north");
   public static final PropertyBool CONNECTED_SOUTH = PropertyBool.create("connected_south");
   public static final PropertyBool CONNECTED_WEST = PropertyBool.create("connected_west");
   public static final PropertyBool CONNECTED_EAST = PropertyBool.create("connected_east");

   public BlockConnectedTexture(Material material) {
      super(material);
      this.setDefaultState(this.blockState.getBaseState().withProperty(CONNECTED_DOWN, Boolean.FALSE).withProperty(CONNECTED_EAST, Boolean.FALSE).withProperty(CONNECTED_NORTH, Boolean.FALSE).withProperty(CONNECTED_SOUTH, Boolean.FALSE).withProperty(CONNECTED_UP, Boolean.FALSE).withProperty(CONNECTED_WEST, Boolean.FALSE));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {
      return state.withProperty(CONNECTED_DOWN, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.DOWN))).withProperty(CONNECTED_EAST, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.EAST))).withProperty(CONNECTED_NORTH, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.NORTH))).withProperty(CONNECTED_SOUTH, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.SOUTH))).withProperty(CONNECTED_UP, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.UP))).withProperty(CONNECTED_WEST, Boolean.valueOf(this.isSideConnectable(state, world, position, EnumFacing.WEST)));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST});
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   private boolean isSideConnectable(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      IBlockState connected = world.getBlockState(pos.offset(side));
      return connected != null && this.canConnect(state, connected);
   }

   protected boolean canConnect(@Nonnull IBlockState original, @Nonnull IBlockState connected) {
      return original.getBlock() == connected.getBlock();
   }
}
