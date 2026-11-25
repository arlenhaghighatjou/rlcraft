package reborncore.api.praescriptum.ingredients.input;

import net.minecraft.item.ItemStack;
import reborncore.common.util.ItemUtils;

public class ItemStackInputIngredient extends InputIngredient<ItemStack> {
   public static ItemStackInputIngredient of(ItemStack ingredient) {
      return new ItemStackInputIngredient(ingredient);
   }

   public static ItemStackInputIngredient of(ItemStack ingredient, boolean consumable) {
      return new ItemStackInputIngredient(ingredient, consumable);
   }

   public static ItemStackInputIngredient copyOf(ItemStack ingredient) {
      return new ItemStackInputIngredient(ingredient.copy());
   }

   public static ItemStackInputIngredient copyOf(ItemStack ingredient, boolean consumable) {
      return new ItemStackInputIngredient(ingredient.copy(), consumable);
   }

   protected ItemStackInputIngredient(ItemStack ingredient) {
      super(ingredient);
   }

   protected ItemStackInputIngredient(ItemStack ingredient, boolean consumable) {
      super(ingredient, consumable);
   }

   public Object getUnspecific() {
      return ((ItemStack)this.ingredient).getItem();
   }

   public InputIngredient<ItemStack> copy() {
      return of(((ItemStack)this.ingredient).copy());
   }

   public boolean isEmpty() {
      return ItemUtils.isEmpty((ItemStack)this.ingredient);
   }

   public int getCount() {
      return ItemUtils.getSize((ItemStack)this.ingredient);
   }

   public void shrink(int amount) {
      ((ItemStack)this.ingredient).shrink(amount);
   }

   public boolean matches(Object other) {
      return other instanceof ItemStack && ItemUtils.isItemEqual((ItemStack)this.ingredient, (ItemStack)other, true, false);
   }

   public boolean matchesStrict(Object other) {
      return other instanceof ItemStack && ItemUtils.isItemEqual((ItemStack)this.ingredient, (ItemStack)other, true, true);
   }

   public String toFormattedString() {
      return ItemUtils.toFormattedString((ItemStack)this.ingredient);
   }

   public boolean equals(Object object) {
      if (this.getClass() != object.getClass()) {
         return false;
      } else {
         return this.matches(((ItemStackInputIngredient)object).ingredient) && this.consumable == ((ItemStackInputIngredient)object).consumable;
      }
   }
}
