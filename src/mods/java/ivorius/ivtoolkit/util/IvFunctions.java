package ivorius.ivtoolkit.util;

import com.google.common.collect.ImmutableMultimap;
import ivorius.ivtoolkit.tools.GuavaCollectors;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class IvFunctions {
   public IvFunctions() {
   }

   public static <T> Collection<Collection<T>> group(List<T> list, Function<T, Object> group) {
      return groupMap(list, group).asMap().values();
   }

   public static <K, T> ImmutableMultimap<K, T> groupMap(List<T> list, Function<T, K> group) {
      return (ImmutableMultimap)list.stream().collect(GuavaCollectors.toMultimap(group, Collections::singleton));
   }
}
