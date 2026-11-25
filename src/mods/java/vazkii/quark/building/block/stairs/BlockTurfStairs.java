package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.Turf;

public class BlockTurfStairs extends BlockQuarkStairs implements Turf.ITurfBlock {
   public BlockTurfStairs() {
      super("turf_stairs", Turf.turf.getDefaultState());
   }
}
