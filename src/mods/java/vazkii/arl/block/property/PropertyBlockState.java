package vazkii.arl.block.property;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyBlockState implements IUnlistedProperty<IBlockState> {
   public PropertyBlockState() {
   }

   public String getName() {
      return "state";
   }

   public boolean isValid(IBlockState value) {
      return true;
   }

   public Class<IBlockState> getType() {
      return IBlockState.class;
   }

   public String valueToString(IBlockState value) {
      return value.toString();
   }
}
