package ivorius.ivtoolkit.maze.classic;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Stack;

public class MazeGeneratorClassic {
   public static final byte NULL = 0;
   public static final byte INVALID = 1;
   public static final byte WALL = 2;
   public static final byte ROOM = 3;

   public MazeGeneratorClassic() {
   }

   public static Maze<Byte> initialize(int... dimensions) {
      return new Maze<Byte>(Byte.class, 1, 0, dimensions);
   }

   public static void generateStartPathsForEnclosedMaze(Maze<Byte> maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform) {
      MazeGenerator.generateStartPathsForEnclosedMaze(maze, startPoints, blockedRooms, transform, Byte.valueOf((byte)3), Byte.valueOf((byte)3), Byte.valueOf((byte)2));
   }

   public static void generatePaths(EaglercraftRandom rand, Maze<Byte> maze, int[] pathWeights, MazeRoom startPoint) {
      for(int i = 0; i < maze.dimensions.length; ++i) {
         if (maze.dimensions[i] < 3) {
            return;
         }
      }

      MazeRoom position = startPoint;
      maze.set(Byte.valueOf((byte)3), startPoint);
      Stack<MazeRoom> positionStack = new Stack();
      ArrayList<MazePath> validRoomNeighbors = new ArrayList();

      while(true) {
         validRoomNeighbors.clear();

         for(MazePath neighbor : MazeCoordinates.getNeighborPaths(position)) {
            if (maze.isNull(maze.get(neighbor.getDestinationRoom()))) {
               for(int n = 0; n < pathWeights[neighbor.getPathDimension()]; ++n) {
                  validRoomNeighbors.add(neighbor);
               }
            }
         }

         if (validRoomNeighbors.size() == 0) {
            if (positionStack.empty()) {
               for(int i = 0; i < ((Byte[])maze.blocks).length; ++i) {
                  if (maze.isInvalidOrNull(((Byte[])maze.blocks)[i])) {
                     ((Byte[])maze.blocks)[i] = 2;
                  }
               }

               return;
            }

            position = (MazeRoom)positionStack.pop();
         } else {
            positionStack.push(position);
            MazePath usedPath = (MazePath)validRoomNeighbors.get(rand.nextInt(validRoomNeighbors.size()));
            MazeRoom destRoom = usedPath.getDestinationRoom();
            maze.set(Byte.valueOf((byte)3), usedPath);
            maze.set(Byte.valueOf((byte)3), destRoom);
            MazePath[] neighbors = MazeCoordinates.getNeighborPaths(position);

            for(MazePath neighbor : neighbors) {
               if (maze.isInvalid(maze.get(neighbor))) {
                  maze.set(Byte.valueOf((byte)2), neighbor);
               }
            }

            position = destRoom;
         }
      }
   }
}
