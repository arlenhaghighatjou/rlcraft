package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.Lists;
import ivorius.ivtoolkit.IvToolkit;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.random.WeightedShuffler;
import ivorius.ivtoolkit.util.IvFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;

public class MazeComponentConnector {
   public static int INFINITE_REVERSES = -1;

   public MazeComponentConnector() {
   }

   public static <M extends WeightedMazeComponent<C>, C> List<PlacedMazeComponent<M, C>> connect(MorphingMazeComponent<C> maze, List<M> components, ConnectionStrategy<C> connectionStrategy, MazePredicate<C> predicate, EaglercraftRandom random, int reverses) {
      List<MultiMazeComponent<M, C>> multis = (List)IvFunctions.group(components, (c) -> {
         return Triple.of(c.rooms(), c.exits(), c.reachability());
      }).stream().map((c) -> {
         return new MultiMazeComponent(Lists.newArrayList(c));
      }).collect(Collectors.toList());
      return (List)connectMulti(maze, multis, connectionStrategy, predicate, random, reverses).stream().map((placed) -> {
         return ((MultiMazeComponent)placed.component()).place(placed.shift(), random);
      }).collect(Collectors.toList());
   }

   protected static <M extends MultiMazeComponent<MO, C>, MO extends WeightedMazeComponent<C>, C> List<PlacedMazeComponent<M, C>> connectMulti(MorphingMazeComponent<C> maze, List<M> components, ConnectionStrategy<C> connectionStrategy, MazePredicate<C> predicate, EaglercraftRandom random, int reverses) {
      List<MazeComponentConnector.ReverseInfo<M, C>> placeOrder = new ArrayList();
      MazeComponentConnector.ReverseInfo<M, C> reversing = null;
      List<PlacedMazeComponent<M, C>> result = new ArrayList();
      LinkedList<Triple<MazeRoom, MazePassage, C>> exitStack = new LinkedList();
      Predicate<ShiftedMazeComponent<M, C>> componentPredicate = ((input) -> {
         return !MazeComponents.overlap(maze, input);
      }).and((input) -> {
         return predicate.canPlace(maze, input);
      });
      addAllExits(predicate, exitStack, maze.exits().entrySet(), random);

      while(exitStack.size() > 0) {
         if (reversing == null) {
            if (maze.rooms().contains(((Triple)exitStack.peekLast()).getLeft())) {
               exitStack.removeLast();
               continue;
            }

            reversing = new MazeComponentConnector.ReverseInfo<M, C>();
            reversing.exitStack = (LinkedList)exitStack.clone();
            reversing.maze = maze.copy();
            reversing.shuffleSeed = random.nextLong();
         } else {
            predicate.willUnplace(maze, reversing.placed);
            exitStack = (LinkedList)reversing.exitStack.clone();
            maze.set(reversing.maze);
            predicate.didUnplace(maze, reversing.placed);
            result.remove(result.size() - 1);
         }

         Triple<MazeRoom, MazePassage, C> triple = (Triple)exitStack.removeLast();
         MazeRoom room = (MazeRoom)triple.getLeft();
         MazePassage exit = (MazePassage)triple.getMiddle();
         C connection = (C)triple.getRight();
         ToDoubleFunction<ShiftedMazeComponent<M, C>> weightFunction = (shifted) -> {
            return ((MultiMazeComponent)shifted.getComponent()).getWeight() * (double)MazeComponents.connectWeight(maze, shifted, connectionStrategy);
         };
         List<WeightedSelector.SimpleItem<ShiftedMazeComponent<M, C>>> shiftedComponents = (List)components.stream().flatMap(MazeComponents.shiftAllFunction(exit, connection, connectionStrategy)).map((input) -> {
            return new WeightedSelector.SimpleItem<PlacedMazeComponent<M, C>>(weightFunction.applyAsDouble(input), input);
         }).filter((w) -> {
            return w.getWeight() >= 0.0D;
         }).collect(Collectors.toCollection(ArrayList::new));
         ShiftedMazeComponent<M, C> placing = null;
         if (reversing.triedIndices > shiftedComponents.size()) {
            throw new RuntimeException("Maze component selection not static.");
         }

         if (reversing.triedIndices < shiftedComponents.size()) {
            Iterator<ShiftedMazeComponent<M, C>> shuffler = WeightedShuffler.<ShiftedMazeComponent<M, C>>iterateShuffled(new EaglercraftRandom(reversing.shuffleSeed), shiftedComponents);

            for(int i = 0; i < reversing.triedIndices; ++i) {
               shuffler.next();
            }

            for(ShiftedMazeComponent<M, C> comp : () -> {
               return shuffler;
            }) {
               ++reversing.triedIndices;
               if (componentPredicate.test(comp)) {
                  placing = comp;
                  break;
               }
            }
         }

         if (placing == null) {
            if (reverses == 0) {
               IvToolkit.logger.warn("Did not find fitting component for maze!");
               IvToolkit.logger.warn("Suggested: X with exits " + maze.exits().entrySet().stream().filter(entryConnectsTo(room)).collect(Collectors.toList()));
               reversing = null;
            } else {
               if (reverses > 0) {
                  --reverses;
               }

               if (placeOrder.size() == 0) {
                  IvToolkit.logger.warn("Maze is not completable!");
                  IvToolkit.logger.warn("Switching to flawed mode.");
                  reverses = 0;
                  reversing = null;
               } else {
                  reversing = (MazeComponentConnector.ReverseInfo)placeOrder.remove(placeOrder.size() - 1);
               }
            }
         } else {
            reversing.placed = placing;
            predicate.willPlace(maze, placing);
            addAllExits(predicate, exitStack, placing.exits().entrySet(), random);
            maze.add(placing);
            result.add(new PlacedMazeComponent((WeightedMazeComponent)placing.getComponent(), placing.getShift()));
            predicate.didPlace(maze, placing);
            placeOrder.add(reversing);
            reversing = null;
         }
      }

      return result;
   }

   private static Predicate<Entry<MazePassage, ?>> entryConnectsTo(MazeRoom finalRoom) {
      return (input) -> {
         return input != null && ((MazePassage)input.getKey()).has(finalRoom);
      };
   }

   private static <M extends WeightedMazeComponent<C>, C> void addAllExits(MazePredicate<C> placementStrategy, List<Triple<MazeRoom, MazePassage, C>> exitStack, Set<Entry<MazePassage, C>> entries, EaglercraftRandom random) {
      for(Entry<MazePassage, C> exit : entries) {
         MazePassage connection = (MazePassage)exit.getKey();
         C c = (C)exit.getValue();
         if (placementStrategy.isDirtyConnection(connection.getLeft(), connection.getRight(), c)) {
            addRandomly(exitStack, random, connection, c, connection.getLeft());
         }

         if (placementStrategy.isDirtyConnection(connection.getRight(), connection.getLeft(), c)) {
            addRandomly(exitStack, random, connection, c, connection.getRight());
         }
      }

   }

   private static <C> void addRandomly(List<Triple<MazeRoom, MazePassage, C>> exitStack, EaglercraftRandom random, MazePassage connection, C c, MazeRoom left) {
      exitStack.add(random.nextInt(exitStack.size() + 1), Triple.of(left, connection, c));
   }

   private static class ReverseInfo<M extends WeightedMazeComponent<C>, C> {
      public long shuffleSeed;
      public int triedIndices;
      public MorphingMazeComponent<C> maze;
      public LinkedList<Triple<MazeRoom, MazePassage, C>> exitStack;
      public ShiftedMazeComponent<M, C> placed;

      private ReverseInfo() {
      }
   }
}
