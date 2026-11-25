package crafttweaker.api.item;

import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class IngredientOr implements IIngredient {
   private final IIngredient[] elements;
   private final String mark;
   private final IItemCondition[] conditions;
   private final IItemTransformerNew[] transformerNews;
   private final IItemTransformer[] transformer;

   public IngredientOr(IIngredient[] elements) {
      this.elements = elements;
      this.mark = null;
      this.conditions = ArrayUtil.EMPTY_CONDITIONS;
      this.transformerNews = ArrayUtil.EMPTY_TRANSFORMERS;
      this.transformer = ArrayUtil.EMPTY_TRANSFORMERS_NEW;
   }

   public IngredientOr(IIngredient a, IIngredient b) {
      this(new IIngredient[]{a, b});
   }

   private IngredientOr(IIngredient[] elements, String mark, IItemCondition[] conditions, IItemTransformerNew[] transformerNews, IItemTransformer[] transformers) {
      this.elements = elements;
      this.mark = mark;
      this.conditions = conditions;
      this.transformerNews = transformerNews;
      this.transformer = transformers;
   }

   public String getMark() {
      return this.mark;
   }

   public int getAmount() {
      return this.elements[0].getAmount();
   }

   public List<IItemStack> getItems() {
      List<IItemStack> result = new ArrayList();

      for(IIngredient element : this.elements) {
         if (element != null) {
            result.addAll(element.getItems());
         } else if (result.contains((Object)null)) {
            result.add((Object)null);
         }
      }

      return result;
   }

   public IItemStack[] getItemArray() {
      List<IItemStack> items = this.getItems();
      return (IItemStack[])items.toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      List<ILiquidStack> result = new ArrayList();

      for(IIngredient element : this.elements) {
         if (element != null) {
            result.addAll(element.getLiquids());
         }
      }

      return result;
   }

   public IIngredient amount(int amount) {
      IIngredient[] result = new IIngredient[this.elements.length];

      for(int i = 0; i < this.elements.length; ++i) {
         if (this.elements[i] != null) {
            result[i] = this.elements[i].amount(amount);
         }
      }

      return new IngredientOr(result);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientOr(this.elements, this.mark, this.conditions, ArrayUtil.append(this.transformerNews, transformer), this.transformer);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientOr(this.elements, this.mark, ArrayUtil.append(this.conditions, condition), this.transformerNews, this.transformer);
   }

   public IIngredient marked(String mark) {
      return new IngredientOr(this.elements, mark, this.conditions, this.transformerNews, this.transformer);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(ArrayUtil.append(this.elements, ingredient));
   }

   public boolean matches(IItemStack item) {
      for(IIngredient ingredient : this.elements) {
         if (ingredient != null && ingredient.matches(item) || ingredient == null && item == null) {
            for(IItemCondition condition : this.conditions) {
               if (!condition.matches(item)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   public boolean matchesExact(IItemStack item) {
      for(IIngredient ingredient : this.elements) {
         if (ingredient != null && ingredient.matchesExact(item) || ingredient == null && item == null) {
            for(IItemCondition condition : this.conditions) {
               if (!condition.matches(item)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   public boolean matches(ILiquidStack liquid) {
      for(IIngredient ingredient : this.elements) {
         if (ingredient == null) {
            if (liquid == null) {
               return true;
            }
         } else if (ingredient.matches(liquid)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(IIngredient ingredient) {
      if (ingredient != null) {
         for(IItemStack item : ingredient.getItems()) {
            if (!this.matches(item)) {
               return false;
            }
         }

         return true;
      } else {
         return this.matches((IItemStack)null) || this.matches((ILiquidStack)null);
      }
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      for(IItemTransformer transformer : this.transformer) {
         item = transformer.transform(item, byPlayer);
      }

      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      boolean changed = false;

      for(IIngredient element : this.elements) {
         if (element != null && element.matches(item) && element.hasNewTransformers()) {
            item = element.applyNewTransform(item);
            changed = true;
         }
      }

      if (this.transformerNews.length == 0 && !changed) {
         return ItemStackUnknown.INSTANCE;
      } else {
         for(IItemTransformerNew transformer : this.transformerNews) {
            item = transformer.transform(item);
         }

         return item;
      }
   }

   public boolean hasNewTransformers() {
      for(IIngredient element : this.elements) {
         if (element != null && element.hasNewTransformers()) {
            return true;
         }
      }

      return this.transformerNews.length > 0;
   }

   public boolean hasTransformers() {
      return this.transformer.length != 0;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientOr(this.elements, this.mark, this.conditions, this.transformerNews, ArrayUtil.append(this.transformer, transformer));
   }

   public Object getInternal() {
      return this.elements;
   }

   public String toCommandString() {
      if (this.elements.length == 0) {
         return "ERROR";
      } else {
         StringJoiner joiner = new StringJoiner(" | ");

         for(IIngredient element : this.elements) {
            joiner.add(element == null ? "null" : element.toCommandString());
         }

         return joiner.toString();
      }
   }

   public String toString() {
      return this.toCommandString();
   }
}
