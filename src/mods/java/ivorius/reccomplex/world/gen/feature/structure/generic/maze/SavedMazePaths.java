package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SavedMazePaths {
   public SavedMazePaths() {
   }

   public static Function<SavedMazePathConnection, Entry<MazePassage, Connector>> buildFunction(Environment environment, ConnectorFactory factory) {
      return (input) -> {
         return input != null ? input.build(environment, factory) : null;
      };
   }

   public static <K, V> void put(Map<K, V> map, Entry<K, V> entry) {
      map.put(entry.getKey(), entry.getValue());
   }

   public static <K, V> void putAll(Map<K, V> map, Iterable<Entry<K, V>> entries) {
      for(Entry<K, V> entry : entries) {
         map.put(entry.getKey(), entry.getValue());
      }

   }

   public static Stream<SavedMazePath> neighborPaths(MazeRoom room, IntStream dimensions) {
      return dimensions.mapToObj((d) -> {
         return IntStream.of(new int[]{1, -1}).mapToObj((m) -> {
            return new SavedMazePath(d, room, m > 0);
         });
      }).flatMap((t) -> {
         return t;
      });
   }

   public static Stream<SavedMazePath> neighborPaths(MazeRoom room) {
      return neighborPaths(room, IntStream.range(0, room.getDimensions()));
   }
}
