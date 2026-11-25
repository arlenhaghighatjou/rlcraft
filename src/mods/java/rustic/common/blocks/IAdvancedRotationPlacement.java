package rustic.common.blocks;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public interface IAdvancedRotationPlacement {
   default IBlockState getStateForAdvancedRotationPlacement(IBlockState defaultState, PropertyDirection facingProperty, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState state;
      if (facing.getAxis() == Axis.Y) {
         float hitXFromCenter = hitX - 0.5F;
         float hitZFromCenter = hitZ - 0.5F;
         if (Math.max(Math.abs(hitXFromCenter), Math.abs(hitZFromCenter)) == Math.abs(hitXFromCenter)) {
            state = defaultState.withProperty(facingProperty, hitXFromCenter > 0.0F ? EnumFacing.EAST : EnumFacing.WEST);
         } else {
            state = defaultState.withProperty(facingProperty, hitZFromCenter > 0.0F ? EnumFacing.SOUTH : EnumFacing.NORTH);
         }
      } else {
         state = defaultState.withProperty(facingProperty, facing.getOpposite());
      }

      return state;
   }
}
