package ivorius.ivtoolkit.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IvLists {
   public IvLists() {
   }

   @SafeVarargs
   public static <S, D> List<D> enumerate(S obj, Function<S, D>... functions) {
      return (List)Arrays.stream(functions).map((func) -> {
         return (List<D>)func.apply(obj);
      }).collect(Collectors.toList());
   }
}
