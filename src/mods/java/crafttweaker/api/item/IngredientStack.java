package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class IngredientStack implements IIngredient {
   private final IIngredient ingredient;
   private final int amount;

   public IngredientStack(IIngredient ingredient, int amount) {
      this.ingredient = ingredient;
      this.amount = amount;
   }

   public String getMark() {
      return this.ingredient.getMark();
   }

   public int getAmount() {
      return this.amount;
   }

   public List<IItemStack> getItems() {
      return this.ingredient.getItems();
   }

   public IItemStack[] getItemArray() {
      IItemStack[] stacks = this.ingredient.getItemArray();

      for(int i = 0; i < stacks.length; ++i) {
         stacks[i] = stacks[i].withAmount(this.getAmount());
      }

      return stacks;
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.emptyList();
   }

   public IIngredient amount(int amount) {
      return new IngredientStack(this.ingredient, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientStack(this.ingredient.transformNew(transformer), this.amount);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientStack(this.ingredient.only(condition), this.amount);
   }

   public IIngredient marked(String mark) {
      return new IngredientStack(this.ingredient.marked(mark), this.amount);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      return item != null && item.getAmount() >= this.getAmount() && this.ingredient.matches(item);
   }

   public boolean matchesExact(IItemStack item) {
      return item != null && item.getAmount() >= this.getAmount() && this.ingredient.matchesExact(item);
   }

   public boolean matches(ILiquidStack liquid) {
      return false;
   }

   public boolean contains(IIngredient ingredient) {
      return this.ingredient.contains(ingredient);
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return this.ingredient.applyTransform(item, byPlayer);
   }

   public IItemStack applyNewTransform(IItemStack item) {
      return this.ingredient.applyNewTransform(item);
   }

   public Object getInternal() {
      return this.ingredient;
   }

   public String toCommandString() {
      return this.ingredient.toCommandString() + " * " + this.amount;
   }

   public boolean hasNewTransformers() {
      return this.ingredient.hasNewTransformers();
   }

   public boolean hasTransformers() {
      return this.ingredient.hasTransformers();
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientStack(this.ingredient.transform(transformer), this.amount);
   }

   public String toString() {
      StringJoiner stringJoiner = new StringJoiner(",", "(Ingredients) ", "");

      for(IItemStack item : this.ingredient.getItems()) {
         stringJoiner.add(item.getName());
      }

      return stringJoiner.toString();
   }
}
