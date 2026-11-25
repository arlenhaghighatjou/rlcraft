package shadows.placebo.util;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StackPrimer {
   Item item;
   Block block;
   final int size;
   final int meta;

   public StackPrimer(Item item, int size, int meta) {
      this.item = null;
      this.item = item;
      this.size = size;
      this.meta = meta;
   }

   public StackPrimer(Item item, int size) {
      this(item, size, 0);
   }

   public StackPrimer(Item item) {
      this(item, 1, 0);
   }

   public StackPrimer(Block block, int size, int meta) {
      this.item = null;
      this.block = block;
      this.size = size;
      this.meta = meta;
   }

   public StackPrimer(Block item, int size) {
      this(item, size, 0);
   }

   public StackPrimer(Block item) {
      this(item, 1, 0);
   }

   public ItemStack genStack() {
      return new ItemStack(this.item == null ? (this.item = Item.getItemFromBlock(this.block)) : this.item, this.size, this.meta);
   }

   public boolean isEmpty() {
      return this.item == null || this.size <= 0;
   }

   @Nullable
   public Block getBlock() {
      return this.block;
   }

   @Nullable
   public Item getItem() {
      return this.item;
   }

   public int getCount() {
      return this.size;
   }

   public int getMeta() {
      return this.meta;
   }
}
