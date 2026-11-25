package ivorius.ivtoolkit.maze.components;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

public class MazeRoomConnections {
   public MazeRoomConnections() {
   }

   public static MazeRoomConnection rotated(MazeRoomConnection connection, AxisAlignedTransform2D transform, int[] size) {
      return new MazeRoomConnection(MazeRooms.rotated(connection.getLeft(), transform, size), MazeRooms.rotated(connection.getRight(), transform, size));
   }
}
