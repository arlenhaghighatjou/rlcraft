package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import java.util.Collections;
import java.util.List;

public class IngredientUnknown implements IIngredient {
   public static final IngredientUnknown INSTANCE = new IngredientUnknown(1);
   private final int size;

   public IngredientUnknown(int size) {
      this.size = size;
   }

   public String getMark() {
      return null;
   }

   public int getAmount() {
      return this.size;
   }

   public List<IItemStack> getItems() {
      return Collections.emptyList();
   }

   public IItemStack[] getItemArray() {
      return new IItemStack[0];
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.emptyList();
   }

   public IIngredient amount(int amount) {
      return new IngredientUnknown(amount);
   }

   public IIngredient or(IIngredient ingredient) {
      return ingredient;
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return this;
   }

   public IIngredient only(IItemCondition condition) {
      return this;
   }

   public IIngredient marked(String mark) {
      return this;
   }

   public boolean matches(IItemStack item) {
      return false;
   }

   public boolean matchesExact(IItemStack item) {
      return false;
   }

   public boolean matches(ILiquidStack liquid) {
      return false;
   }

   public boolean contains(IIngredient ingredient) {
      return false;
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      return item;
   }

   public boolean hasNewTransformers() {
      return false;
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return this;
   }

   public Object getInternal() {
      return null;
   }

   public String toCommandString() {
      return "Error: UnknownIngredient";
   }
}
