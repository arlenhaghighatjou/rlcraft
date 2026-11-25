package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules;

import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MorphingMazeComponent;
import ivorius.ivtoolkit.maze.components.ShiftedMazeComponent;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class LimitAABBStrategy<C> implements MazePredicate<C>, Predicate<MazeRoom> {
   @Nonnull
   private int[] roomNumbers;

   public LimitAABBStrategy(@Nonnull int[] roomNumbers) {
      this.roomNumbers = roomNumbers;
   }

   public boolean canPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      return component.rooms().stream().allMatch(this);
   }

   public void willPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   public void didPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   public void willUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   public void didUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   public boolean isDirtyConnection(MazeRoom dest, MazeRoom source, C c) {
      return this.test(dest);
   }

   public boolean test(MazeRoom input) {
      for(int i = 0; i < input.getDimensions(); ++i) {
         if (input.getCoordinate(i) < 0 || input.getCoordinate(i) >= this.roomNumbers[i]) {
            return false;
         }
      }

      return true;
   }
}
