package codersafterdark.compatskills.common.compats.minecraft.item.harvestlevel;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class BlockHarvestLock implements FuzzyLockKey {
   private final int harvestLevel;

   public BlockHarvestLock(int harvestLevel) {
      this.harvestLevel = harvestLevel;
   }

   public BlockHarvestLock(ItemStack stack) {
      Block block = Block.getBlockFromItem(stack.getItem());
      this.harvestLevel = block == Blocks.AIR ? 0 : block.getHarvestLevel(block.getDefaultState());
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      return o == this || o instanceof BlockHarvestLock && this.harvestLevel >= ((BlockHarvestLock)o).harvestLevel;
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof BlockHarvestLock && this.harvestLevel == ((BlockHarvestLock)o).harvestLevel;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.harvestLevel});
   }
}
