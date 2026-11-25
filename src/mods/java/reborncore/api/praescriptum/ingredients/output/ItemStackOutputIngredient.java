package reborncore.api.praescriptum.ingredients.output;

import net.minecraft.item.ItemStack;
import reborncore.common.util.ItemUtils;

public class ItemStackOutputIngredient extends OutputIngredient<ItemStack> {
   public static ItemStackOutputIngredient of(ItemStack ingredient) {
      return new ItemStackOutputIngredient(ingredient);
   }

   public static ItemStackOutputIngredient copyOf(ItemStack ingredient) {
      return new ItemStackOutputIngredient(ingredient.copy());
   }

   protected ItemStackOutputIngredient(ItemStack ingredient) {
      super(ingredient);
   }

   public OutputIngredient<ItemStack> copy() {
      return of(((ItemStack)this.ingredient).copy());
   }

   public boolean isEmpty() {
      return ItemUtils.isEmpty((ItemStack)this.ingredient);
   }

   public boolean matches(Object other) {
      return !(other instanceof ItemStack) ? false : ItemUtils.isItemEqual((ItemStack)this.ingredient, (ItemStack)other, true, true);
   }

   public boolean matchesStrict(Object other) {
      return this.matches(other);
   }

   public String toFormattedString() {
      return ItemUtils.toFormattedString((ItemStack)this.ingredient);
   }

   public boolean equals(Object object) {
      return this.getClass() != object.getClass() ? false : this.matches(((ItemStackOutputIngredient)object).ingredient);
   }

   public int getCount() {
      return ItemUtils.getSize((ItemStack)this.ingredient);
   }
}
