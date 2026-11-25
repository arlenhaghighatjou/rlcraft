package ivorius.ivtoolkit.maze.classic;

public class MazeCoordinateDirect implements MazeCoordinate {
   public int[] coordinates;

   public MazeCoordinateDirect(int... coordinates) {
      this.coordinates = coordinates;
   }

   public int[] getMazeCoordinates() {
      return this.coordinates;
   }
}
