package ivorius.reccomplex.maze;

import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MazeRoomConnection;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

public class MazeInterpreter {
   public MazeInterpreter() {
   }

   public static <C> void print2D(MazeComponent<C> component, MazePredicate<C> predicate, MazeRoom... mark) {
      print2D(component, predicate, (r) -> {
         return (void)(Arrays.asList(mark).contains(r) ? "O" : null);
      });
   }

   public static <C> void print2D(MazeComponent<C> component, MazePredicate<C> predicate, Function<MazeRoom, String> marker) {
      int minY = component.rooms().stream().mapToInt((r) -> {
         return (void)r.getCoordinate(1);
      }).min().orElseThrow(IllegalArgumentException::new);
      int maxY = component.rooms().stream().mapToInt((r) -> {
         return (void)r.getCoordinate(1);
      }).max().orElseThrow(IllegalArgumentException::new);

      for(int y = minY; y <= maxY; ++y) {
         System.out.print(toString(component, predicate, 0, 2, new MazeRoom(new int[]{0, y, 0}), marker));
      }

   }

   public static <C> String toString(MazeComponent<C> component, @Nullable MazePredicate<C> predicate, int xDim, int zDim, MazeRoom pos, @Nullable Function<MazeRoom, String> marker) {
      int minX = component.rooms().stream().mapToInt((r) -> {
         return r.getCoordinate(xDim);
      }).min().orElseThrow(IllegalArgumentException::new) - 1;
      int minZ = component.rooms().stream().mapToInt((r) -> {
         return r.getCoordinate(zDim);
      }).min().orElseThrow(IllegalArgumentException::new) - 1;
      int maxX = component.rooms().stream().mapToInt((r) -> {
         return r.getCoordinate(xDim);
      }).max().orElseThrow(IllegalArgumentException::new) + 1;
      int maxZ = component.rooms().stream().mapToInt((r) -> {
         return r.getCoordinate(zDim);
      }).max().orElseThrow(IllegalArgumentException::new) + 1;
      StringBuilder builder = new StringBuilder();
      builder.append(" ");

      for(int x = minX; x <= maxX; ++x) {
         builder.append((x % 10 + 10) % 10).append(" ");
      }

      builder.append("\n");

      for(int z = minZ; z <= maxZ; ++z) {
         builder.append((z % 10 + 10) % 10);

         for(int x = minX; x <= maxX; ++x) {
            MazeRoom mazeRoom = setInDimension(setInDimension(pos, zDim, z), xDim, x);
            String marked = marker != null ? (String)marker.apply(mazeRoom) : null;
            String rep;
            if (marked != null) {
               rep = marked;
            } else if (component.rooms().contains(mazeRoom)) {
               rep = "X";
            } else {
               rep = " ";
            }

            MazeRoom next = mazeRoom.addInDimension(xDim, 1);
            if (x < maxX && isConnection(component, new MazeRoomConnection(mazeRoom, next))) {
               builder.append(rep).append("-");
            } else {
               builder.append(rep).append(" ");
            }
         }

         if (z < maxZ) {
            builder.append("\n ");

            for(int x = minX; x <= maxX; ++x) {
               MazeRoom mazeRoom = setInDimension(setInDimension(pos, zDim, z), xDim, x);
               MazeRoom next = mazeRoom.addInDimension(zDim, 1);
               String rep = " ";
               if (isConnection(component, new MazeRoomConnection(mazeRoom, next))) {
                  rep = "|";
               }

               builder.append(rep).append(" ");
            }
         }

         builder.append("\n");
      }

      return builder.toString();
   }

   protected static <C> boolean isExit(MazeComponent<C> component, @Nullable MazePredicate<C> predicate, MazeRoom mazeRoom) {
      return component.exits().keySet().stream().filter((p) -> {
         return p.getDest().equals(mazeRoom) || p.getSource().equals(mazeRoom);
      }).anyMatch((p) -> {
         return predicate != null && predicate.isDirtyConnection(p.getSource(), p.getDest(), component.exits().get(p));
      });
   }

   protected static <C> boolean isConnection(MazeComponent<C> component, MazeRoomConnection connection) {
      MazePassage fst = new MazePassage(connection.getLeft(), connection.getRight());
      MazePassage sec = new MazePassage(connection.getRight(), connection.getLeft());
      return component.reachability().get(fst).contains(sec) || component.reachability().get(sec).contains(fst);
   }

   protected static MazeRoom setInDimension(MazeRoom pos, int dim, int val) {
      return pos.addInDimension(dim, val - pos.getCoordinate(dim));
   }
}
