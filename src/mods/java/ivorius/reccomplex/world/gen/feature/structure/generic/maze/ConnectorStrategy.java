package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import ivorius.ivtoolkit.maze.components.ConnectionStrategy;
import ivorius.ivtoolkit.maze.components.MazePassage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConnectorStrategy implements ConnectionStrategy<Connector> {
   public static final String DEFAULT_WALL = "Wall";
   public static final String DEFAULT_PATH = "Path";

   public ConnectorStrategy() {
   }

   public float connect(@Nonnull MazePassage connection, @Nullable Connector existing, @Nonnull Connector add) {
      return add.accepts(existing) ? 1.0F : -1.0F;
   }
}
