package com.blamejared.compat.mantle;

import com.google.common.collect.Lists;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatch.Match;

public class RecipeMatchIIngredient extends RecipeMatch {
   private IIngredient ingredient;
   private List<ItemStack> items;

   public RecipeMatchIIngredient(IIngredient ingredient) {
      this(ingredient, 1);
   }

   public RecipeMatchIIngredient(IIngredient ingredient, int amountMatched) {
      super(amountMatched, ingredient.getAmount());
      this.ingredient = ingredient;
   }

   public List<ItemStack> getInputs() {
      return this.items != null ? this.items : (this.items = (List)Arrays.stream(this.ingredient.getItemArray()).map(CraftTweakerMC::getItemStack).collect(Collectors.toList()));
   }

   public Optional<Match> matches(NonNullList<ItemStack> stacks) {
      List<ItemStack> found = Lists.newLinkedList();
      int stillNeeded = this.amountNeeded;

      for(ItemStack stack : stacks) {
         if (this.ingredient.matches(CraftTweakerMC.getIItemStack(stack))) {
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(copy.getCount(), stillNeeded));
            found.add(copy);
            stillNeeded -= copy.getCount();
            if (stillNeeded <= 0) {
               return Optional.of(new Match(found, this.amountMatched));
            }
         }
      }

      return Optional.empty();
   }
}
