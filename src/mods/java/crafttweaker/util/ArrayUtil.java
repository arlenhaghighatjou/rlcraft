package crafttweaker.util;

import crafttweaker.api.block.IBlockPattern;
import crafttweaker.api.block.IBlockStateMatcher;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import java.util.Arrays;

public class ArrayUtil {
   public static final IItemCondition[] EMPTY_CONDITIONS = new IItemCondition[0];
   public static final IItemTransformerNew[] EMPTY_TRANSFORMERS = new IItemTransformerNew[0];
   public static final IItemTransformer[] EMPTY_TRANSFORMERS_NEW = new IItemTransformer[0];

   public ArrayUtil() {
   }

   public static IItemCondition[] append(IItemCondition[] values, IItemCondition value) {
      IItemCondition[] result = (IItemCondition[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }

   public static IItemTransformer[] append(IItemTransformer[] values, IItemTransformer value) {
      IItemTransformer[] result = (IItemTransformer[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }

   public static IItemTransformerNew[] append(IItemTransformerNew[] values, IItemTransformerNew value) {
      IItemTransformerNew[] result = (IItemTransformerNew[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }

   public static IIngredient[] append(IIngredient[] values, IIngredient value) {
      IIngredient[] result = (IIngredient[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }

   public static IBlockPattern[] append(IBlockPattern[] values, IBlockPattern value) {
      IBlockPattern[] result = (IBlockPattern[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }

   public static IBlockStateMatcher[] append(IBlockStateMatcher[] values, IBlockStateMatcher value) {
      IBlockStateMatcher[] result = (IBlockStateMatcher[])Arrays.copyOf(values, values.length + 1);
      result[values.length] = value;
      return result;
   }
}
