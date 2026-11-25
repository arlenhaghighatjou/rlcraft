package ivorius.ivtoolkit.maze.components;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MazeRooms {
   public MazeRooms() {
   }

   public static MazeRoom rotated(MazeRoom room, AxisAlignedTransform2D transform, int[] size) {
      if (room.getDimensions() < 3) {
         throw new IllegalArgumentException();
      } else {
         return new MazeRoom(transform.applyOn(room.getCoordinates(), size, 1));
      }
   }

   public static Stream<MazeRoom> neighbors(MazeRoom room, IntStream dimensions) {
      return dimensions.mapToObj((d) -> {
         return IntStream.of(new int[]{1, -1}).mapToObj((m) -> {
            return room.addInDimension(d, m);
         });
      }).flatMap((t) -> {
         return t;
      });
   }

   public static Stream<MazeRoom> neighbors(MazeRoom room) {
      return neighbors(room, IntStream.range(0, room.getDimensions()));
   }

   public static Stream<MazePassage> neighborPassages(MazeRoom room, IntStream dimensions) {
      return neighbors(room, dimensions).map((r) -> {
         return new MazePassage(room, r);
      });
   }

   public static Stream<MazePassage> neighborPassages(MazeRoom room) {
      return neighbors(room).map((r) -> {
         return new MazePassage(room, r);
      });
   }
}
