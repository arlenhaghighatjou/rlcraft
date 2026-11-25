package crafttweaker.api.oredict;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.ArrayUtil;
import java.util.Collections;
import java.util.List;

public class IngredientOreDict implements IIngredient {
   private final IOreDictEntry entry;
   private final String mark;
   private final IItemCondition[] conditions;
   private final IItemTransformerNew[] transformerNews;
   private final IItemTransformer[] transformers;

   public IngredientOreDict(IOreDictEntry entry, String mark, IItemCondition[] conditions, IItemTransformerNew[] transformerNews, IItemTransformer[] transformers) {
      this.entry = entry;
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
      return this.entry.getItems();
   }

   public IItemStack[] getItemArray() {
      List<IItemStack> items = this.getItems();
      return (IItemStack[])items.toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.emptyList();
   }

   public IIngredient amount(int amount) {
      return new IngredientStack(this, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientOreDict(this.entry, this.mark, this.conditions, ArrayUtil.append(this.transformerNews, transformer), this.transformers);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientOreDict(this.entry, this.mark, ArrayUtil.append(this.conditions, condition), this.transformerNews, this.transformers);
   }

   public IIngredient marked(String mark) {
      return new IngredientOreDict(this.entry, mark, this.conditions, this.transformerNews, this.transformers);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      if (!this.entry.matches(item)) {
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
      if (!this.entry.matchesExact(item)) {
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
      return this.entry.contains(ingredient);
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      for(IItemTransformer transformer : this.transformers) {
         item = transformer.transform(item, byPlayer);
      }

      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      for(IItemTransformerNew transformer : this.transformerNews) {
         item = transformer.transform(item);
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
      return new IngredientOreDict(this.entry, this.mark, this.conditions, this.transformerNews, (IItemTransformer[])stanhebben.zenscript.util.ArrayUtil.add(this.transformers, transformer));
   }

   public Object getInternal() {
      return this.entry.getInternal();
   }

   public String toCommandString() {
      return this.entry.toCommandString();
   }
}
