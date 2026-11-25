package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules;

import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MorphingMazeComponent;
import ivorius.ivtoolkit.maze.components.ShiftedMazeComponent;
import java.util.Collection;
import javax.annotation.Nonnull;

public class BlockedConnectorStrategy<C> implements MazePredicate<C> {
   @Nonnull
   private Collection<C> ignoredConnections;

   public BlockedConnectorStrategy(@Nonnull Collection<C> ignoredConnections) {
      this.ignoredConnections = ignoredConnections;
   }

   public boolean canPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      return true;
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
      return !this.ignoredConnections.contains(c);
   }
}
