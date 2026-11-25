package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.IronPlates;

public class BlockIronPlateStairs extends BlockQuarkStairs {
   public BlockIronPlateStairs() {
      super("iron_plate_stairs", IronPlates.iron_plate.getDefaultState());
   }
}
