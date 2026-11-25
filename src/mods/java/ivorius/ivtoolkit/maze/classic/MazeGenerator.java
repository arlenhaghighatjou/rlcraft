package ivorius.ivtoolkit.maze.classic;

import com.google.common.collect.Lists;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MazeGenerator {
   public MazeGenerator() {
   }

   public static <T> void blockRoomAndExits(Maze<T> maze, MazeRoom coordinate, T room, T blockedPath) {
      maze.set(room, coordinate);

      for(MazePath neighbor : MazeCoordinates.getNeighborPaths(coordinate)) {
         maze.replace(maze.nullValue, blockedPath, neighbor);
      }

   }

   public static <T> void generateStartPathsForEnclosedMaze(Maze<T> maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform, T path, T room, T blockedPath) {
      int[] mazeSizeInRooms = new int[maze.dimensions.length];

      for(int i = 0; i < mazeSizeInRooms.length; ++i) {
         mazeSizeInRooms[i] = (maze.dimensions[i] - 1) / 2;
      }

      maze.allPaths().stream().filter(maze::isPathPointingOutside).forEach((exitPath) -> {
         maze.set(blockedPath, exitPath);
      });

      for(MazeRoom blockedRoom : blockedRooms) {
         blockedRoom = MazeCoordinates.rotatedRoom(blockedRoom, transform, mazeSizeInRooms);
         blockRoomAndExits(maze, blockedRoom, room, blockedPath);
      }

      for(MazePath startPoint : startPoints) {
         maze.set(path, MazeCoordinates.rotatedPath(startPoint, transform, mazeSizeInRooms));
      }

   }

   public static <T> MazePath randomEmptyPathInMaze(Random rand, Maze<T> maze, Collection<Integer> applicableDimensions) {
      List<MazePath> paths = Lists.newArrayList(maze.allPaths());
      Iterator<MazePath> iterator = paths.iterator();

      while(iterator.hasNext()) {
         MazePath path = (MazePath)iterator.next();
         if (!maze.isNull(maze.get(path)) || !applicableDimensions.contains(Integer.valueOf(path.getPathDimension()))) {
            iterator.remove();
         }
      }

      if (paths.size() > 0) {
         return (MazePath)paths.get(rand.nextInt(paths.size()));
      } else {
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public static MazeRoom randomRoomInMaze(Random rand, Maze maze, int... distanceFromOutside) {
      int[] position = new int[maze.dimensions.length];

      for(int i = 0; i < maze.dimensions.length; ++i) {
         position[i] = rand.nextInt(maze.dimensions[i] / 2 - distanceFromOutside[i]);
      }

      return new MazeRoom(position);
   }

   /** @deprecated */
   @Deprecated
   public static MazePath randomPathInMaze(Random rand, Maze maze, int... distanceFromOutside) {
      List<WeightedSelector.SimpleItem<Integer>> dimensionWeights = new ArrayList();

      for(int dim = 0; dim < maze.dimensions.length; ++dim) {
         int dimLength = maze.dimensions[dim] / 2 + 1 - (distanceFromOutside[dim] + 1) / 2 * 2;
         dimensionWeights.add(new WeightedSelector.SimpleItem((double)Math.max(0, dimLength), dim));
      }

      int usedDimension = ((Integer)WeightedSelector.select(rand, dimensionWeights)).intValue();
      int[] roomDistanceFromOutside = new int[distanceFromOutside.length];

      for(int i = 0; i < roomDistanceFromOutside.length; ++i) {
         roomDistanceFromOutside[i] = distanceFromOutside[i] / 2;
      }

      MazeRoom refRoom = randomRoomInMaze(rand, maze, roomDistanceFromOutside);
      int[] pathCoord = refRoom.getMazeCoordinates();
      int availablePaths = (distanceFromOutside[usedDimension] + 1) / 2 * 2;
      pathCoord[usedDimension] = rand.nextInt(availablePaths) * 2 + (maze.dimensions[usedDimension] / 2 + 1 - availablePaths) / 2;
      return MazeCoordinates.coordToPath(new MazeCoordinateDirect(pathCoord), usedDimension);
   }
}
