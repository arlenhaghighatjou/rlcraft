package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.ReedBlock;

public class BlockReedStairs extends BlockQuarkStairs {
   public BlockReedStairs() {
      super("reed_block_stairs", ReedBlock.reed_block.getDefaultState());
   }
}
