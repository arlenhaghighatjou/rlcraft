package shadows.placebo.block;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import shadows.placebo.interfaces.IItemBlock;
import shadows.placebo.interfaces.IPropertyEnum;
import shadows.placebo.interfaces.ISpecialPlacement;

public interface IEnumBlock<E extends Enum<E> & IPropertyEnum> extends IEnumBlockAccess<E>, ISpecialPlacement, IItemBlock {
   List<E> getTypes();

   BlockStateContainer createStateContainer();

   BlockStateContainer getRealStateContainer();

   PropertyEnum<E> getProperty();

   default E getValue(IBlockState state) {
      return (E)(state.getValue(this.getProperty()));
   }

   default boolean canPlaceBlockAt(IBlockState state, World world, BlockPos pos, EnumFacing side) {
      return ((Block)this).canPlaceBlockAt(world, pos);
   }
}
