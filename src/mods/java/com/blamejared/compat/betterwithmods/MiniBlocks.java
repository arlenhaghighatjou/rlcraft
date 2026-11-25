package com.blamejared.compat.betterwithmods;

import betterwithmods.module.gameplay.miniblocks.MiniBlockIngredient;
import com.blamejared.compat.betterwithmods.base.bulkrecipes.BulkRecipeBuilder;
import com.google.common.collect.Lists;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.MiniBlocks")
@ModOnly("betterwithmods")
@ZenRegister
public class MiniBlocks {
   public MiniBlocks() {
   }

   @ZenMethod
   public static IIngredient getMiniBlock(String type, IIngredient parent) {
      MiniBlockIngredient mini = new MiniBlockIngredient(type, BulkRecipeBuilder.getIngredient(parent));
      return new MiniBlocks.IngredientMini(mini, 1);
   }

   public static class IngredientMini implements IIngredient {
      private final MiniBlockIngredient ingredient;
      private final int amount;

      public IngredientMini(MiniBlockIngredient ingredient, int amount) {
         this.ingredient = ingredient;
         this.amount = amount;
      }

      public String getMark() {
         return null;
      }

      public int getAmount() {
         return this.amount;
      }

      public List<IItemStack> getItems() {
         return Lists.newArrayList(this.getItemArray());
      }

      public IItemStack[] getItemArray() {
         return CraftTweakerMC.getIItemStacks(this.ingredient.getMatchingStacks());
      }

      public List<ILiquidStack> getLiquids() {
         return Collections.emptyList();
      }

      public IIngredient amount(int amount) {
         return new MiniBlocks.IngredientMini(this.ingredient, amount);
      }

      public IIngredient transformNew(IItemTransformerNew transformer) {
         return null;
      }

      public IIngredient only(IItemCondition condition) {
         return null;
      }

      public IIngredient marked(String mark) {
         return null;
      }

      public IIngredient or(IIngredient ingredient) {
         return new IngredientOr(this, ingredient);
      }

      public boolean matches(IItemStack item) {
         return this.ingredient.apply(CraftTweakerMC.getItemStack(item));
      }

      public boolean matchesExact(IItemStack item) {
         return this.matches(item);
      }

      public boolean matches(ILiquidStack liquid) {
         return false;
      }

      public boolean contains(IIngredient ingredient) {
         return ingredient.getItems().stream().anyMatch(this::matches);
      }

      public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
         return item;
      }

      public IItemStack applyNewTransform(IItemStack item) {
         return item;
      }

      public Object getInternal() {
         return null;
      }

      public String toCommandString() {
         return this.ingredient.toString() + " * " + this.amount;
      }

      public boolean hasNewTransformers() {
         return false;
      }

      public boolean hasTransformers() {
         return false;
      }

      public IIngredient transform(IItemTransformer transformer) {
         return null;
      }

      public String toString() {
         return "(Ingredients) " + this.ingredient.toString();
      }
   }
}
