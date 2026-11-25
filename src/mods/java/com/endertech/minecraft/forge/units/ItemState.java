package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.items.ForgeStack;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Immutable
public class ItemState implements IWithMeta<ItemState> {
   public static final ItemState EMPTY = from((Item)null, 0);
   @Nullable
   private final Item item;
   private final int meta;

   protected ItemState(@Nullable Item item, int meta) {
      this.item = item;
      this.meta = meta;
   }

   public static ItemState from(@Nullable Item item, int meta) {
      return new ItemState(item, meta);
   }

   public static ItemState from(@Nullable Item item) {
      return from(item, 0);
   }

   public static ItemState from(ItemStack stack) {
      return from(stack.getItem(), stack.getMetadata());
   }

   @Nullable
   public Item getItem() {
      return this.item;
   }

   public int getMeta() {
      return this.meta;
   }

   public boolean exists() {
      return this.getItem() != null;
   }

   public boolean matches(ItemState state) {
      return state.getItem() == this.getItem() && UnitId.metasAreMatched(state.getMeta(), this.getMeta());
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getItem(), this.getMeta()});
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof ItemState)) {
         return super.equals(obj);
      } else {
         ItemState state = (ItemState)obj;
         return state.getItem() == this.getItem() && state.getMeta() == this.getMeta();
      }
   }

   public ItemStack toItemStack(int amount) {
      return new ItemStack(this.getItem(), amount, this.getMeta());
   }

   public ForgeStack toStack(int amount) {
      return ForgeStack.from(this, amount);
   }

   public String toString() {
      return (this.exists() ? this.getItem().getUnlocalizedName() : "null") + "@" + this.getMeta();
   }

   public ItemState withMeta(int meta) {
      return from(this.getItem(), meta);
   }
}
