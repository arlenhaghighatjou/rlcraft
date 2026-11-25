package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Immutable
public class ForgeStack extends ItemState {
   public static final ForgeStack EMPTY = from(ItemState.EMPTY, 0);
   private final int amount;

   protected ForgeStack(Item item, int meta, int amount) {
      super(item, meta);
      this.amount = amount;
   }

   public static ForgeStack from(Item item, int meta, int amount) {
      return new ForgeStack(item, meta, amount);
   }

   public static ForgeStack from(Item item, int amount) {
      return from(item, 0, amount);
   }

   public static ForgeStack from(Item item) {
      return from(item, 1);
   }

   public static ForgeStack from(ItemState state, int amount) {
      return from(state.getItem(), state.getMeta(), amount);
   }

   public static ForgeStack from(ItemStack stack) {
      return from(stack.getItem(), stack.getMetadata(), stack.getCount());
   }

   public static ForgeStack from(UnitId id, int amount) {
      return from(id.getItemState(), amount);
   }

   public int getAmount() {
      return this.amount;
   }

   public boolean matches(ForgeStack stack) {
      return super.matches(stack) && stack.getAmount() == this.getAmount();
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.getAmount()});
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof ForgeStack)) {
         return super.equals(obj);
      } else {
         return super.equals(obj) && this.getAmount() == ((ForgeStack)obj).getAmount();
      }
   }

   public ItemStack toItemStack() {
      return super.toItemStack(this.getAmount());
   }

   public ForgeStack toStack() {
      return super.toStack(this.getAmount());
   }

   public String toString() {
      return this.getAmount() + "x" + super.toString();
   }
}
