package ivorius.ivtoolkit.maze.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MazePredicateMany<C> implements MazePredicate<C> {
   public final List<MazePredicate<C>> predicates = new ArrayList();

   public MazePredicateMany() {
   }

   public MazePredicateMany(List<MazePredicate<C>> predicates) {
      this.predicates.addAll(predicates);
   }

   @SafeVarargs
   public MazePredicateMany(MazePredicate<C>... predicates) {
      Collections.addAll(this.predicates, predicates);
   }

   public boolean canPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      return this.predicates.stream().allMatch((p) -> {
         return p.canPlace(maze, component);
      });
   }

   public void willPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.predicates.forEach((p) -> {
         p.willPlace(maze, component);
      });
   }

   public void didPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.predicates.forEach((p) -> {
         p.didPlace(maze, component);
      });
   }

   public void willUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.predicates.forEach((p) -> {
         p.willUnplace(maze, component);
      });
   }

   public void didUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.predicates.forEach((p) -> {
         p.didUnplace(maze, component);
      });
   }

   public boolean isDirtyConnection(MazeRoom dest, MazeRoom source, C c) {
      return this.predicates.stream().allMatch((p) -> {
         return p.isDirtyConnection(dest, source, c);
      });
   }
}
