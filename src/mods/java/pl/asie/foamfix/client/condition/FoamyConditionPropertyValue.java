package pl.asie.foamfix.client.condition;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.multipart.ConditionPropertyValue;

public class FoamyConditionPropertyValue extends ConditionPropertyValue {
   private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();

   public FoamyConditionPropertyValue(String keyIn, String valueIn) {
      super(keyIn, valueIn);
   }

   public Predicate<IBlockState> getPredicate(BlockStateContainer blockState) {
      IProperty<?> property = blockState.getProperty(this.key);
      if (property == null) {
         throw new RuntimeException(this.toString() + ": Definition: " + blockState + " has no property: " + this.key);
      } else {
         String s = this.value;
         boolean negate = !s.isEmpty() && s.charAt(0) == '!';
         if (negate) {
            s = s.substring(1);
         }

         List<String> valueNames = SPLITTER.splitToList(s);
         if (valueNames.isEmpty()) {
            throw new RuntimeException(this.toString() + ": has an empty value: " + this.value);
         } else {
            Object[] values = new Object[valueNames.size()];

            for(int i = 0; i < values.length; ++i) {
               Optional<?> valueParsed = property.parseValue((String)valueNames.get(i));
               if (!valueParsed.isPresent()) {
                  throw new RuntimeException(this.toString() + ": has an unknown value: " + this.value);
               }

               values[i] = valueParsed.get();
            }

            if (negate) {
               return (Predicate<IBlockState>)(values.length > 1 ? new FoamyConditionPropertyValue.PredicateNegative(property, values) : new FoamyConditionPropertyValue.SingletonPredicateNegative(property, values[0]));
            } else {
               return (Predicate<IBlockState>)(values.length > 1 ? new FoamyConditionPropertyValue.PredicatePositive(property, values) : new FoamyConditionPropertyValue.SingletonPredicatePositive(property, values[0]));
            }
         }
      }
   }

   public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionPropertyValue> {
      public HashingStrategy() {
      }

      public int computeHashCode(FoamyConditionPropertyValue object) {
         return 31 * object.key.hashCode() + object.value.hashCode();
      }

      public boolean equals(FoamyConditionPropertyValue o1, FoamyConditionPropertyValue o2) {
         return o2.key.equals(o1.key) && o2.value.equals(o1.value);
      }
   }

   public static final class PredicateNegative implements Predicate<IBlockState> {
      private final IProperty<?> property;
      private final Object[] values;

      protected PredicateNegative(IProperty<?> property, Object[] values) {
         this.property = property;
         this.values = values;
      }

      public boolean apply(@Nullable IBlockState state) {
         if (state != null) {
            Object value = state.getValue(this.property);

            for(int i = 0; i < this.values.length; ++i) {
               if (value.equals(this.values[i])) {
                  return false;
               }
            }
         }

         return true;
      }

      public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionPropertyValue.PredicateNegative> {
         public HashingStrategy() {
         }

         public int computeHashCode(FoamyConditionPropertyValue.PredicateNegative object) {
            return 31 * object.property.hashCode() + Arrays.hashCode(object.values);
         }

         public boolean equals(FoamyConditionPropertyValue.PredicateNegative o1, FoamyConditionPropertyValue.PredicateNegative o2) {
            if (o1.property == o2.property) {
               if (o1.values.length != o2.values.length) {
                  return false;
               } else {
                  for(int i = 0; i < o1.values.length; ++i) {
                     if (o1.values[i] != o2.values[i]) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static final class PredicatePositive implements Predicate<IBlockState> {
      private final IProperty<?> property;
      private final Object[] values;

      protected PredicatePositive(IProperty<?> property, Object[] values) {
         this.property = property;
         this.values = values;
      }

      public boolean apply(@Nullable IBlockState state) {
         if (state != null) {
            Object value = state.getValue(this.property);

            for(int i = 0; i < this.values.length; ++i) {
               if (value.equals(this.values[i])) {
                  return true;
               }
            }
         }

         return false;
      }

      public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionPropertyValue.PredicatePositive> {
         public HashingStrategy() {
         }

         public int computeHashCode(FoamyConditionPropertyValue.PredicatePositive object) {
            return 31 * object.property.hashCode() + Arrays.hashCode(object.values);
         }

         public boolean equals(FoamyConditionPropertyValue.PredicatePositive o1, FoamyConditionPropertyValue.PredicatePositive o2) {
            if (o1.property == o2.property) {
               if (o1.values.length != o2.values.length) {
                  return false;
               } else {
                  for(int i = 0; i < o1.values.length; ++i) {
                     if (o1.values[i] != o2.values[i]) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static final class SingletonPredicateNegative implements Predicate<IBlockState> {
      private final IProperty<?> property;
      private final Object value;

      protected SingletonPredicateNegative(IProperty<?> property, Object value) {
         this.property = property;
         this.value = value;
      }

      public boolean apply(@Nullable IBlockState state) {
         return state == null || !state.getValue(this.property).equals(this.value);
      }

      public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionPropertyValue.SingletonPredicateNegative> {
         public HashingStrategy() {
         }

         public int computeHashCode(FoamyConditionPropertyValue.SingletonPredicateNegative object) {
            return 31 * object.property.hashCode() + (object.value != null ? object.value.hashCode() : 0);
         }

         public boolean equals(FoamyConditionPropertyValue.SingletonPredicateNegative o1, FoamyConditionPropertyValue.SingletonPredicateNegative o2) {
            return o1.property == o2.property && o1.value == o2.value;
         }
      }
   }

   public static final class SingletonPredicatePositive implements Predicate<IBlockState> {
      private final IProperty<?> property;
      private final Object value;

      protected SingletonPredicatePositive(IProperty<?> property, Object value) {
         this.property = property;
         this.value = value;
      }

      public boolean apply(@Nullable IBlockState state) {
         return state != null && state.getValue(this.property).equals(this.value);
      }

      public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionPropertyValue.SingletonPredicatePositive> {
         public HashingStrategy() {
         }

         public int computeHashCode(FoamyConditionPropertyValue.SingletonPredicatePositive object) {
            return 31 * object.property.hashCode() + (object.value != null ? object.value.hashCode() : 0);
         }

         public boolean equals(FoamyConditionPropertyValue.SingletonPredicatePositive o1, FoamyConditionPropertyValue.SingletonPredicatePositive o2) {
            return o1.property == o2.property && o1.value == o2.value;
         }
      }
   }
}
