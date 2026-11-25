package ivorius.ivtoolkit.maze.components;

public interface MorphingMazeComponent<C> extends MazeComponent<C> {
   void add(MazeComponent<C> var1);

   void set(MazeComponent<C> var1);

   MorphingMazeComponent<C> copy();
}
