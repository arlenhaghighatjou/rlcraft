package ivorius.ivtoolkit.maze.components;

public interface MazePredicate<C> {
   boolean canPlace(MorphingMazeComponent<C> var1, ShiftedMazeComponent<?, C> var2);

   void willPlace(MorphingMazeComponent<C> var1, ShiftedMazeComponent<?, C> var2);

   void didPlace(MorphingMazeComponent<C> var1, ShiftedMazeComponent<?, C> var2);

   void willUnplace(MorphingMazeComponent<C> var1, ShiftedMazeComponent<?, C> var2);

   void didUnplace(MorphingMazeComponent<C> var1, ShiftedMazeComponent<?, C> var2);

   boolean isDirtyConnection(MazeRoom var1, MazeRoom var2, C var3);
}
