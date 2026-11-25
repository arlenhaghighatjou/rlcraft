package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.ArrayUtil;
import java.util.Collections;
import java.util.List;

public class IngredientAnyAdvanced implements IIngredient {
   private final String mark;
   private final IItemCondition[] conditions;
   private final IItemTransformerNew[] transformerNews;
   private final IItemTransformer[] transformers;

   public IngredientAnyAdvanced(String mark, IItemCondition[] conditions, IItemTransformerNew[] transformerNews, IItemTransformer[] transformers) {
      this.mark = mark;
      this.conditions = conditions;
      this.transformerNews = transformerNews;
      this.transformers = transformers;
   }

   public String getMark() {
      return this.mark;
   }

   public int getAmount() {
      return 1;
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
      return new IngredientStack(this, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientAnyAdvanced(this.mark, this.conditions, ArrayUtil.append(this.transformerNews, transformer), this.transformers);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientAnyAdvanced(this.mark, ArrayUtil.append(this.conditions, condition), this.transformerNews, this.transformers);
   }

   public IIngredient marked(String mark) {
      return new IngredientAnyAdvanced(mark, this.conditions, this.transformerNews, this.transformers);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      for(IItemCondition condition : this.conditions) {
         if (!condition.matches(item)) {
            return false;
         }
      }

      return true;
   }

   public boolean matchesExact(IItemStack item) {
      for(IItemCondition condition : this.conditions) {
         if (!condition.matches(item)) {
            return false;
         }
      }

      return true;
   }

   public boolean matches(ILiquidStack liquid) {
      return true;
   }

   public boolean contains(IIngredient ingredient) {
      if (ingredient == null) {
         return this.matches((IItemStack)null);
      } else {
         for(IItemStack item : ingredient.getItems()) {
            if (!this.matches(item)) {
               return false;
            }
         }

         return true;
      }
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
      return new IngredientAnyAdvanced(this.mark, this.conditions, this.transformerNews, (IItemTransformer[])stanhebben.zenscript.util.ArrayUtil.add(this.transformers, transformer));
   }

   public Object getInternal() {
      return IngredientAny.INTERNAL_ANY;
   }

   public String toCommandString() {
      return "<*>";
   }
}
