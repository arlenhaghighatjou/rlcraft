package ivorius.ivtoolkit.maze.components;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConnectionStrategy<T> {
   float connect(@Nonnull MazePassage var1, @Nullable T var2, @Nonnull T var3);
}
