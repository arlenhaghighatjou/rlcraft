package slimeknights.mantle.block;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class BlockStairsBase extends BlockStairs {
   public BlockStairsBase(IBlockState modelState) {
      super(modelState);
      this.useNeighborBrightness = true;
      this.setCreativeTab(modelState.getBlock().displayOnCreativeTab);
   }
}
