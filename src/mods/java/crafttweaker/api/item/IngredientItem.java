package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.ArrayUtil;
import java.util.Collections;
import java.util.List;

public class IngredientItem implements IIngredient {
   private final IItemStack item;
   private final String mark;
   private final IItemCondition[] conditions;
   private final IItemTransformerNew[] transformerNews;
   private final List<IItemStack> items;
   private final IItemTransformer[] transformers;

   public IngredientItem(IItemStack item, String mark, IItemCondition[] conditions, IItemTransformerNew[] transformerNews, IItemTransformer[] transformers) {
      this.item = item;
      this.mark = mark;
      this.conditions = conditions;
      this.transformerNews = transformerNews;
      this.items = Collections.singletonList(item);
      this.transformers = transformers;
   }

   public String getMark() {
      return this.mark;
   }

   public int getAmount() {
      return this.item.getAmount();
   }

   public List<IItemStack> getItems() {
      return this.items;
   }

   public IItemStack[] getItemArray() {
      return (IItemStack[])this.items.toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.emptyList();
   }

   public IIngredient amount(int amount) {
      return new IngredientStack(this, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientItem(this.item, this.mark, this.conditions, ArrayUtil.append(this.transformerNews, transformer), this.transformers);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientItem(this.item, this.mark, ArrayUtil.append(this.conditions, condition), this.transformerNews, this.transformers);
   }

   public IIngredient marked(String mark) {
      return new IngredientItem(this.item, mark, this.conditions, this.transformerNews, this.transformers);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      if (!this.item.matches(item)) {
         return false;
      } else {
         for(IItemCondition condition : this.conditions) {
            if (!condition.matches(item)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean matchesExact(IItemStack item) {
      if (!this.item.matchesExact(item)) {
         return false;
      } else {
         for(IItemCondition condition : this.conditions) {
            if (!condition.matches(item)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean matches(ILiquidStack liquid) {
      return false;
   }

   public boolean contains(IIngredient ingredient) {
      for(IItemStack iitem : ingredient.getItems()) {
         if (!this.matches(iitem)) {
            return false;
         }
      }

      return true;
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      for(IItemTransformer transform : this.transformers) {
         item = transform.transform(item, byPlayer);
      }

      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      for(IItemTransformerNew transform : this.transformerNews) {
         item = transform.transform(item);
      }

      return item;
   }

   public boolean hasNewTransformers() {
      return this.transformerNews.length > 0;
   }

   public boolean hasTransformers() {
      return this.transformers.length > 0;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientItem(this.item, this.mark, this.conditions, this.transformerNews, (IItemTransformer[])stanhebben.zenscript.util.ArrayUtil.add(this.transformers, transformer));
   }

   public Object getInternal() {
      return this.item.getInternal();
   }

   public String toString() {
      return "(Ingredient) " + this.item.toString();
   }

   public String toCommandString() {
      return this.item.toCommandString();
   }
}
