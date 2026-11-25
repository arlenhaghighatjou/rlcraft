package ivorius.reccomplex.utils.leveled;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeveledBiMap<K, V> extends LeveledMap<K, V> {
   public LeveledBiMap(int levels) {
      super(HashBiMap.create(), (Collection)IntStream.range(0, levels).mapToObj((i) -> {
         return HashBiMap.create();
      }).collect(Collectors.toList()));
   }

   public BiMap<K, V> getMap() {
      return (BiMap)this.map;
   }

   public BiMap<K, V> getMap(int level) {
      return (BiMap)super.getMap(level);
   }
}
