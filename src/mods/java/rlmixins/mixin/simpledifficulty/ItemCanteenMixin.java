package rlmixins.mixin.simpledifficulty;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.item.ItemCanteen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ItemCanteen.class})
public abstract class ItemCanteenMixin {
   public ItemCanteenMixin() {
   }

   @ModifyVariable(
      method = {"onItemRightClick"},
      at = @At("STORE"),
      index = 7
   )
   public ThirstEnum rlmixins_simpleDifficultyItemCanteen_onItemRightClick_field1(ThirstEnum value) {
      return value.equals(ThirstEnum.RAIN) ? ThirstEnum.PURIFIED : value;
   }
}
