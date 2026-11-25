package rustic.common.items;

import net.minecraft.block.state.IBlockState;
import rustic.common.blocks.crops.BlockStakeCrop;
import rustic.core.Rustic;

public class ItemStakeCropSeed extends ItemBase {
   private BlockStakeCrop crop;

   public ItemStakeCropSeed(String name, BlockStakeCrop crop) {
      super(name);
      this.setCreativeTab(Rustic.farmingTab);
      this.crop = crop;
   }

   public IBlockState getCropState() {
      return this.crop.getDefaultState();
   }

   public BlockStakeCrop getCrop() {
      return this.crop;
   }
}
