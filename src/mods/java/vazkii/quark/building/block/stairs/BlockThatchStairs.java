package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.Thatch;

public class BlockThatchStairs extends BlockQuarkStairs {
   public BlockThatchStairs() {
      super("thatch_stairs", Thatch.thatch.getDefaultState());
   }
}
