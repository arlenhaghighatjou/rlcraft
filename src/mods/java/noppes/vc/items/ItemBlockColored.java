package noppes.vc.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockColored extends ItemBlock {
   public ItemBlockColored(Block block) {
      super(block);
   }

   public int getMetadata(int damage) {
      return damage;
   }
}
