package ivorius.ivtoolkit.maze.components;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

public class MazePassages {
   public MazePassages() {
   }

   public static MazePassage rotated(MazePassage connection, AxisAlignedTransform2D transform, int[] size) {
      return new MazePassage(MazeRooms.rotated(connection.getSource(), transform, size), MazeRooms.rotated(connection.getDest(), transform, size));
   }
}
