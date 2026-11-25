package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import ivorius.ivtoolkit.tools.GuavaCollectors;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MazeComponents {
   public MazeComponents() {
   }

   public static <M extends MazeComponent<C>, C> Function<M, Stream<ShiftedMazeComponent<M, C>>> shiftAllFunction(MazePassage connection, C connector, ConnectionStrategy<C> strategy) {
      return (component) -> {
         return component.exits().entrySet().stream().map((entry) -> {
            MazeRoom dist = ((MazePassage)entry.getKey()).inverseDistance(connection);
            return dist != null && strategy.connect(connection, connector, entry.getValue()) >= 0.0F ? shift(component, dist) : null;
         }).filter(Objects::nonNull);
      };
   }

   public static <M extends MazeComponent<C>, C> ShiftedMazeComponent<M, C> shift(M component, MazeRoom shift) {
      ImmutableSet<MazeRoom> rooms = (ImmutableSet)component.rooms().stream().map((r) -> {
         return r != null ? r.add(shift) : null;
      }).collect(GuavaCollectors.immutableSet());
      Stream var10000 = component.exits().keySet().stream();
      Function var10001 = (c1) -> {
         return c1 != null ? c1.add(shift) : null;
      };
      Map var10002 = component.exits();
      var10002.getClass();
      ImmutableMap<MazePassage, C> exits = (ImmutableMap)var10000.collect(GuavaCollectors.toMap(var10001, var10002::get));
      ImmutableMultimap<MazePassage, MazePassage> reachability = (ImmutableMultimap)component.reachability().keySet().stream().collect(GuavaCollectors.toMultimap((c) -> {
         return c.add(shift);
      }, (c) -> {
         Stream var10000 = component.reachability().get(c).stream().map((c2) -> {
            return c2.add(shift);
         });
         return var10000::iterator;
      }));
      return new ShiftedMazeComponent<M, C>(component, shift, rooms, exits, reachability);
   }

   public static boolean overlap(MazeComponent<?> left, MazeComponent<?> right) {
      return Sets.intersection(left.rooms(), right.rooms()).size() > 0;
   }

   public static <C> float connectWeight(MazeComponent<C> existing, MazeComponent<C> add, ConnectionStrategy<C> strategy) {
      float total = 1.0F;

      for(Entry<MazePassage, C> entry : add.exits().entrySet()) {
         if ((total *= strategy.connect((MazePassage)entry.getKey(), existing.exits().get(((MazePassage)entry.getKey()).inverse()), entry.getValue())) < 0.0F) {
            return -1.0F;
         }
      }

      return total;
   }
}
