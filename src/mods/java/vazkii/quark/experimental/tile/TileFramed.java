package vazkii.quark.experimental.tile;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vazkii.arl.block.tile.TileSimpleInventory;

public class TileFramed extends TileSimpleInventory {
   private static final IBlockState AIR = Blocks.AIR.getDefaultState();

   public TileFramed() {
   }

   public IBlockState getState() {
      ItemStack stack = this.getStackInSlot(0);
      if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
         ItemBlock block = (ItemBlock)stack.getItem();
         return block.getBlock().getStateFromMeta(stack.getItemDamage());
      } else {
         return AIR;
      }
   }

   public int getSizeInventory() {
      return 1;
   }

   public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
      return itemstack.isEmpty() && itemstack.getItem() instanceof ItemBlock;
   }

   public boolean isAutomationEnabled() {
      return true;
   }
}
