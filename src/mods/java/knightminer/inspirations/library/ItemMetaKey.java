package knightminer.inspirations.library;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMetaKey {
   private Item item;
   private int meta;

   public ItemMetaKey(ItemStack stack) {
      this.item = stack.getItem();
      this.meta = stack.getMetadata();
   }

   public ItemMetaKey(Item item, int meta) {
      this.item = item;
      this.meta = meta;
   }

   public Item getItem() {
      return this.item;
   }

   public int getMeta() {
      return this.meta;
   }

   public ItemStack makeItemStack() {
      return new ItemStack(this.item, 1, this.meta);
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (this.getClass() != other.getClass()) {
         return false;
      } else {
         ItemMetaKey otherKey = (ItemMetaKey)other;
         return otherKey.item == this.item && otherKey.meta == this.meta;
      }
   }

   public int hashCode() {
      int result = this.item != null ? this.item.hashCode() : 0;
      result = 31 * result + this.meta;
      return result;
   }
}
