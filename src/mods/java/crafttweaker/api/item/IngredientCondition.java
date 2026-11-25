package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.item.IIngredient")
@ZenRegister
public class IngredientCondition {
   public IngredientCondition() {
   }

   @ZenMethod
   public static IIngredient onlyDamaged(IIngredient ingredient) {
      return ingredient.only((stack) -> {
         return stack.getDamage() > 0;
      });
   }

   @ZenMethod
   public static IIngredient onlyDamageAtLeast(IIngredient ingredient, int minDamage) {
      return ingredient.only((stack) -> {
         return stack.getDamage() >= minDamage;
      });
   }

   @ZenMethod
   public static IIngredient onlyDamageAtMost(IIngredient ingredient, int maxDamage) {
      return ingredient.only((stack) -> {
         return stack.getDamage() <= maxDamage;
      });
   }

   @ZenMethod
   public static IIngredient onlyDamageBetween(IIngredient ingredient, int minDamage, int maxDamage) {
      return ingredient.only((stack) -> {
         return stack.getDamage() >= minDamage && stack.getDamage() <= maxDamage;
      });
   }

   @ZenMethod
   public static IIngredient onlyWithTag(IIngredient ingredient, IData data) {
      return ingredient.only((stack) -> {
         return stack.getTag() != null && stack.getTag().contains(data);
      });
   }

   @ZenMethod
   public static IIngredient onlyStack(IIngredient ingredient, int amount) {
      return ingredient.only((stack) -> {
         return stack.getAmount() >= amount;
      });
   }
}
