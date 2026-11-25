package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.item.IIngredient")
@ZenRegister
public class IngredientTransform {
   public IngredientTransform() {
   }

   @ZenMethod
   public static IIngredient transformDamage(IIngredient ingredient) {
      return transformDamage(ingredient, 1);
   }

   @ZenMethod
   public static IIngredient transformDamage(IIngredient ingredient, int damage) {
      return ingredient.transformNew((item) -> {
         return item.getDamage() + damage > item.getMaxDamage() ? null : item.withDamage(item.getDamage() + damage);
      });
   }

   @ZenMethod
   public static IIngredient transformReplace(IIngredient ingredient, IItemStack withItem) {
      return ingredient.transformNew((item) -> {
         return withItem;
      });
   }

   @ZenMethod
   public static IIngredient transformConsume(IIngredient ingredient, int amount) {
      return ingredient.transform((item, player) -> {
         int newAmount = Math.max(item.getAmount() - amount, 0);
         return newAmount == 0 ? null : item.withAmount(newAmount);
      });
   }

   @ZenMethod
   public static IIngredient noReturn(IIngredient ingredient) {
      return ingredient.transformNew((item) -> {
         return null;
      });
   }

   @ZenMethod
   public static IIngredient giveBack(IIngredient ingredient, @Optional IItemStack givenItem) {
      return ingredient.transform((item, byPlayer) -> {
         byPlayer.give(givenItem == null ? item : givenItem);
         return null;
      });
   }

   @ZenMethod
   public static IIngredient reuse(IIngredient ingredient) {
      return ingredient.transformNew((item) -> {
         return item.withAmount(1);
      });
   }
}
