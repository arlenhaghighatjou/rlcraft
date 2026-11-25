package snownee.kiwi.crafting.input;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RegularItemStackInput implements ProcessingInput {
   private final Item item;
   private final int meta;
   private final int size;

   public static RegularItemStackInput of(@Nonnull ItemStack instance) {
      return instance.isEmpty() ? of(Items.AIR) : new RegularItemStackInput(instance.getItem(), instance.getMetadata(), instance.getCount());
   }

   public static RegularItemStackInput of(@Nonnull Block block) {
      Item actualForm = Item.getItemFromBlock(block);
      if (actualForm == Items.AIR) {
         throw new IllegalArgumentException("Block %s does not have item form");
      } else {
         return of(actualForm);
      }
   }

   public static RegularItemStackInput of(@Nonnull Block block, int meta) {
      Item actualForm = Item.getItemFromBlock(block);
      if (actualForm == Items.AIR) {
         throw new IllegalArgumentException("Block %s does not have item form");
      } else {
         return of(actualForm, meta);
      }
   }

   public static RegularItemStackInput of(@Nonnull Item item) {
      return new RegularItemStackInput(item, 0, 1);
   }

   public static RegularItemStackInput of(@Nonnull Item item, int meta) {
      return new RegularItemStackInput(item, meta, 1);
   }

   protected RegularItemStackInput(@Nonnull Item item, int meta, int size) {
      this.item = item;
      this.meta = meta;
      this.size = size;
   }

   public List<ItemStack> examples() {
      return this.isEmpty() ? Collections.emptyList() : Collections.singletonList(new ItemStack(this.item, this.size, this.meta));
   }

   public boolean matches(ItemStack stack) {
      return stack.isEmpty() ? this.isEmpty() : stack.getItem() == this.item && stack.getMetadata() == this.meta && stack.getCount() >= this.size;
   }

   public int count() {
      return this.isEmpty() ? 0 : this.size;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RegularItemStackInput that = (RegularItemStackInput)o;
         if (this.meta != that.meta) {
            return false;
         } else if (this.size != that.size) {
            return false;
         } else {
            return this.item == that.item;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.item.hashCode();
      result = 31 * result + this.meta;
      result = 31 * result + this.size;
      return result;
   }

   public boolean isEmpty() {
      return this.item == Items.AIR;
   }
}
