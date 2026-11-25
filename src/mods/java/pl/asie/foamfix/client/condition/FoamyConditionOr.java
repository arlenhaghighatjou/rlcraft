package pl.asie.foamfix.client.condition;

import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.multipart.ICondition;

public class FoamyConditionOr implements ICondition {
   private final Iterable<ICondition> conditions;

   public FoamyConditionOr(Iterable<ICondition> conditionsIn) {
      this.conditions = conditionsIn;
   }

   public Predicate<IBlockState> getPredicate(BlockStateContainer blockState) {
      Predicate[] predicates = (Predicate[])StreamSupport.stream(this.conditions.spliterator(), false).map((cond) -> {
         return cond == null ? null : cond.getPredicate(blockState);
      }).toArray((x$0) -> {
         return new Predicate[x$0];
      });
      return (Predicate<IBlockState>)(predicates.length <= 1 ? predicates[0] : new FoamyConditionOr.PredicateImpl(predicates));
   }

   public static final class PredicateImpl implements Predicate<IBlockState> {
      private final Predicate[] predicates;

      public PredicateImpl(Predicate[] predicates) {
         this.predicates = predicates;
      }

      public boolean apply(@Nullable IBlockState input) {
         for(int i = 0; i < this.predicates.length; ++i) {
            if (this.predicates[i].apply(input)) {
               return true;
            }
         }

         return false;
      }

      public static final class HashingStrategy implements gnu.trove.strategy.HashingStrategy<FoamyConditionOr.PredicateImpl> {
         public HashingStrategy() {
         }

         public int computeHashCode(FoamyConditionOr.PredicateImpl object) {
            return Arrays.hashCode(object.predicates);
         }

         public boolean equals(FoamyConditionOr.PredicateImpl o1, FoamyConditionOr.PredicateImpl o2) {
            return Arrays.equals(o1.predicates, o2.predicates);
         }
      }
   }
}
