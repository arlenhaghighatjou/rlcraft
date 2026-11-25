package ivorius.ivtoolkit.maze.classic;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import net.minecraft.util.math.BlockPos;

public class MazeCoordinates {
   private static TIntObjectMap<MazeRoom[]> cachedNeighborRoomsBlueprints = new TIntObjectHashMap();
   private static TIntObjectMap<MazePath[]> cachedNeighborPathBlueprints = new TIntObjectHashMap();

   public MazeCoordinates() {
   }

   public static MazeRoom rotatedRoom(MazeRoom room, AxisAlignedTransform2D transform, int[] size) {
      int[] roomPosition = room.getCoordinates();
      BlockPos transformedRoom = transform.apply(new BlockPos(roomPosition[0], roomPosition[1], roomPosition[2]), size);
      return new MazeRoom(new int[]{transformedRoom.getX(), transformedRoom.getY(), transformedRoom.getZ()});
   }

   public static MazePath rotatedPath(MazePath path, AxisAlignedTransform2D transform, int[] size) {
      int[] sourceCoords = path.getSourceRoom().getCoordinates();
      int[] destCoords = path.getDestinationRoom().getCoordinates();
      BlockPos transformedSource = transform.apply(new BlockPos(sourceCoords[0], sourceCoords[1], sourceCoords[2]), size);
      BlockPos transformedDest = transform.apply(new BlockPos(destCoords[0], destCoords[1], destCoords[2]), size);
      return MazePath.fromConnection(new MazeRoom(new int[]{transformedSource.getX(), transformedSource.getY(), transformedSource.getZ()}), new MazeRoom(new int[]{transformedDest.getX(), transformedDest.getY(), transformedDest.getZ()}));
   }

   public static boolean[] coordPathFlags(MazeCoordinate coordinate) {
      int[] mazePosition = coordinate.getMazeCoordinates();
      boolean[] flags = new boolean[mazePosition.length];

      for(int i = 0; i < flags.length; ++i) {
         flags[i] = mazePosition[i] % 2 == 0;
      }

      return flags;
   }

   private static MazeRoom[] constructNeighborRooms(int dimensions) {
      MazePath[] neighborPaths = getNeighborPaths(dimensions);
      MazeRoom[] neighbors = new MazeRoom[neighborPaths.length];

      for(int i = 0; i < neighborPaths.length; ++i) {
         neighbors[i] = neighborPaths[i].getDestinationRoom();
      }

      return neighbors;
   }

   public static MazeRoom[] getNeighborRooms(int dimensions) {
      if (!cachedNeighborRoomsBlueprints.containsKey(dimensions)) {
         MazeRoom[] neighbors = constructNeighborRooms(dimensions);
         cachedNeighborRoomsBlueprints.put(dimensions, neighbors);
         return (MazeRoom[])neighbors.clone();
      } else {
         return (MazeRoom[])((MazeRoom[])cachedNeighborRoomsBlueprints.get(dimensions)).clone();
      }
   }

   public static MazePath[] getNeighborPaths(int dimensions) {
      if (cachedNeighborPathBlueprints.containsKey(dimensions)) {
         return (MazePath[])cachedNeighborPathBlueprints.get(dimensions);
      } else {
         MazePath[] neighbors = new MazePath[dimensions * 2];
         int[] coordinates = new int[dimensions];

         for(int i = 0; i < dimensions; ++i) {
            neighbors[i * 2] = MazePath.fromRoom(i, new MazeRoom(coordinates), true);
            neighbors[i * 2 + 1] = MazePath.fromRoom(i, new MazeRoom(coordinates), false);
         }

         cachedNeighborPathBlueprints.put(dimensions, neighbors);
         return neighbors;
      }
   }

   /** @deprecated */
   @Deprecated
   public static MazePath[] getNeighborPaths(int dimensions, MazeRoom mazeRoom) {
      return getNeighborPaths(mazeRoom);
   }

   public static MazePath[] getNeighborPaths(MazeRoom mazeRoom) {
      MazePath[] blueprints = getNeighborPaths(mazeRoom.getDimensions());
      MazePath[] neighbors = new MazePath[blueprints.length];

      for(int i = 0; i < blueprints.length; ++i) {
         neighbors[i] = blueprints[i].add(mazeRoom);
      }

      return neighbors;
   }

   public static MazeRoom coordToRoom(MazeCoordinate coordinate) {
      if (!Maze.isCoordValidRoom(coordinate)) {
         return null;
      } else {
         int[] roomCoord = coordinate.getMazeCoordinates();

         for(int dim = 0; dim < roomCoord.length; ++dim) {
            roomCoord[dim] = (roomCoord[dim] - 1) / 2;
         }

         return new MazeRoom(roomCoord);
      }
   }

   public static MazePath coordToPath(MazeCoordinate coordinate) {
      int pathDim = Maze.getPathDimensionIfPath(coordinate);
      return pathDim >= 0 ? coordToPath(coordinate, pathDim) : null;
   }

   public static MazePath coordToPath(MazeCoordinate coordinate, int pathDim) {
      int[] roomCoord = coordinate.getMazeCoordinates();
      boolean goesUp = true;

      for(int dim = 0; dim < roomCoord.length; ++dim) {
         if (roomCoord[dim] == 0) {
            goesUp = false;
         } else {
            roomCoord[dim] = (roomCoord[dim] - 1) / 2;
         }
      }

      return MazePath.fromRoom(pathDim, new MazeRoom(roomCoord), goesUp);
   }
}
