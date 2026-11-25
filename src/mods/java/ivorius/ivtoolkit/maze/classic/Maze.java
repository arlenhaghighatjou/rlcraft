package ivorius.ivtoolkit.maze.classic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.Logger;

public class Maze<T> {
   public final int[] dimensions;
   public final T[] blocks;
   public final T invalidValue;
   public final T nullValue;
   private List<MazeRoom> cachedRooms;
   private List<MazePath> cachedPaths;

   public Maze(Class<T> tClass, T invalidValue, T nullValue, int... dimensions) {
      int fullLength = 1;

      for(int dimension : dimensions) {
         if (dimension % 2 == 0) {
            throw new IllegalArgumentException("Maze must have enclosing walls! (Odd dimension numbers)");
         }

         fullLength *= dimension;
      }

      this.invalidValue = invalidValue;
      this.nullValue = null;
      this.dimensions = dimensions;
      this.blocks = (T[])((Object[])Array.newInstance(tClass, fullLength));
      if (nullValue != null) {
         Arrays.fill(this.blocks, nullValue);
      }

   }

   public boolean contains(MazeCoordinate coordinate) {
      int[] position = coordinate.getMazeCoordinates();

      for(int i = 0; i < position.length; ++i) {
         if (position[i] < 0 || position[i] >= this.dimensions[i]) {
            return false;
         }
      }

      return true;
   }

   public T get(MazeCoordinate coordinate) {
      return (T)(this.contains(coordinate) ? this.blocks[this.getArrayPosition(coordinate.getMazeCoordinates())] : this.invalidValue);
   }

   public void set(T val, MazeCoordinate coordinates) {
      if (this.contains(coordinates)) {
         this.blocks[this.getArrayPosition(coordinates.getMazeCoordinates())] = val;
      }

   }

   public void replace(T condition, T val, MazeCoordinate coordinates) {
      if (this.contains(coordinates)) {
         int arrayPosition = this.getArrayPosition(coordinates.getMazeCoordinates());
         if (Objects.equals(this.blocks[arrayPosition], condition)) {
            this.blocks[arrayPosition] = val;
         }
      }

   }

   public boolean isInvalid(T value) {
      return Objects.equals(value, this.invalidValue);
   }

   public boolean isNull(T value) {
      return Objects.equals(value, this.nullValue);
   }

   public boolean isInvalidOrNull(T value) {
      return this.isNull(value) || this.isInvalid(value);
   }

   public static int[] getMazeSize(int[] size, int[] pathLengths, int[] roomSize) {
      int[] returnSize = new int[size.length];

      for(int i = 0; i < returnSize.length; ++i) {
         returnSize[i] = (size[i] - pathLengths[i]) / (pathLengths[i] + roomSize[i]) * 2 + 1;
      }

      return returnSize;
   }

   public static int[] getRoomPosition(MazeCoordinate coordinate, int[] pathLengths, int[] roomSize) {
      int[] mazePosition = coordinate.getMazeCoordinates();
      int[] returnPos = new int[pathLengths.length];

      for(int i = 0; i < returnPos.length; ++i) {
         returnPos[i] = mazePosition[i] / 2 * roomSize[i] + (mazePosition[i] + 1) / 2 * pathLengths[i];
      }

      return returnPos;
   }

   public static int[] getRoomSize(int[] rooms, int[] pathLengths, int[] roomSize) {
      int[] returnSize = new int[pathLengths.length];

      for(int i = 0; i < returnSize.length; ++i) {
         returnSize[i] = rooms[i] * roomSize[i] + rooms[i] / 2 * pathLengths[i];
      }

      return returnSize;
   }

   public int[] getCompleteMazeSize(int[] pathLengths, int[] roomWidths) {
      return getRoomPosition(new MazeRoom(this.dimensions), pathLengths, roomWidths);
   }

   public int[] getRoomSize(MazeCoordinate coordinate, int[] pathLengths, int[] roomWidths) {
      int[] returnSize = new int[this.dimensions.length];
      boolean[] isRoomPath = MazeCoordinates.coordPathFlags(coordinate);

      for(int i = 0; i < returnSize.length; ++i) {
         returnSize[i] = isRoomPath[i] ? pathLengths[i] : roomWidths[i];
      }

      return returnSize;
   }

   public static boolean isCoordValidRoom(MazeCoordinate coordinate) {
      boolean[] isRoomPath = MazeCoordinates.coordPathFlags(coordinate);

      for(boolean b : isRoomPath) {
         if (b) {
            return false;
         }
      }

      return true;
   }

   public static int getPathDimensionIfPath(MazeCoordinate coordinate) {
      boolean[] pathFlags = MazeCoordinates.coordPathFlags(coordinate);
      int curDimension = -1;

      for(int dim = 0; dim < pathFlags.length; ++dim) {
         if (pathFlags[dim]) {
            if (curDimension >= 0) {
               return -1;
            }

            curDimension = dim;
         }
      }

      return curDimension;
   }

   public boolean isPathPointingOutside(MazeCoordinate coordinate) {
      int[] mazePosition = coordinate.getMazeCoordinates();

      for(int dim = 0; dim < this.dimensions.length; ++dim) {
         if (mazePosition[dim] == 0 || mazePosition[dim] == this.dimensions[dim] - 1) {
            return true;
         }
      }

      return false;
   }

   public int[] getCoordPosition(int arrayPosition) {
      int[] coordPosition = new int[this.dimensions.length];

      for(int dimension = 0; dimension < this.dimensions.length; ++dimension) {
         coordPosition[dimension] = arrayPosition % this.dimensions[dimension];
         arrayPosition /= this.dimensions[dimension];
      }

      return coordPosition;
   }

   public int getArrayPosition(int... coordPosition) {
      int arrayPosition = 0;
      int multiplier = 1;

      for(int dimension = 0; dimension < this.dimensions.length; ++dimension) {
         arrayPosition += coordPosition[dimension] * multiplier;
         multiplier *= this.dimensions[dimension];
      }

      return arrayPosition;
   }

   public void logMaze2D(Logger logger, int dimension1, int dimension2, MazeCoordinate layerPositon) {
      int[] layerPositionArray = layerPositon.getMazeCoordinates();
      StringBuilder mazeString = new StringBuilder();

      for(int dim = 0; dim < this.dimensions.length; ++dim) {
         if (dim != dimension1 && dim != dimension2 && (dim < 0 || layerPositionArray[dim] >= this.dimensions[dim])) {
            throw new IllegalArgumentException();
         }
      }

      for(int x = 0; x < this.dimensions[dimension1]; ++x) {
         if (x > 0) {
            mazeString.append("\n");
         }

         for(int y = 0; y < this.dimensions[dimension2]; ++y) {
            layerPositionArray[dimension1] = x;
            layerPositionArray[dimension2] = y;
            T type = (T)this.blocks[this.getArrayPosition(layerPositionArray)];
            mazeString.append(type.toString());
         }
      }

      logger.info(mazeString.toString());
   }

   public List<MazeRoom> allRooms() {
      if (this.cachedRooms == null) {
         List<MazeRoom> coordinates = new ArrayList();

         for(int i = 0; i < this.blocks.length; ++i) {
            int[] coord = this.getCoordPosition(i);
            MazeRoom room = MazeCoordinates.coordToRoom(new MazeCoordinateDirect(coord));
            if (room != null) {
               coordinates.add(room);
            }
         }

         this.cachedRooms = coordinates;
      }

      return this.cachedRooms;
   }

   public List<MazePath> allPaths() {
      if (this.cachedPaths == null) {
         List<MazePath> coordinates = new ArrayList();

         for(int i = 0; i < this.blocks.length; ++i) {
            int[] coord = this.getCoordPosition(i);
            MazePath path = MazeCoordinates.coordToPath(new MazeCoordinateDirect(coord));
            if (path != null) {
               coordinates.add(path);
            }
         }

         this.cachedPaths = coordinates;
      }

      return this.cachedPaths;
   }
}
