package ivorius.ivtoolkit.maze.components;

public class PlacedMazeComponent<M extends WeightedMazeComponent<C>, C> {
   private final M component;
   private final MazeRoom shift;

   public PlacedMazeComponent(M component, MazeRoom shift) {
      this.component = component;
      this.shift = shift;
   }

   public M component() {
      return this.component;
   }

   public MazeRoom shift() {
      return this.shift;
   }
}
