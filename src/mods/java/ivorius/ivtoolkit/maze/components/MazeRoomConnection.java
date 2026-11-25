package ivorius.ivtoolkit.maze.components;

import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class MazeRoomConnection extends Pair<MazeRoom, MazeRoom> {
   private final MazeRoom left;
   private final MazeRoom right;

   public MazeRoomConnection(MazeRoom left, MazeRoom right) {
      boolean lower = lower(left, right);
      this.left = lower ? left : right;
      this.right = lower ? right : left;
      if (left.getDimensions() != right.getDimensions()) {
         throw new IllegalArgumentException();
      }
   }

   private static boolean lower(MazeRoom left, MazeRoom right) {
      for(int i = 0; i < left.getDimensions(); ++i) {
         if (left.getCoordinate(i) != right.getCoordinate(i)) {
            return left.getCoordinate(i) < right.getCoordinate(i);
         }
      }

      return false;
   }

   public MazeRoom getLeft() {
      return this.left;
   }

   public MazeRoom getRight() {
      return this.right;
   }

   public int getDimensions() {
      return this.left.getDimensions();
   }

   public MazeRoomConnection add(MazeRoom add) {
      return new MazeRoomConnection(this.left.add(add), this.right.add(add));
   }

   public MazeRoomConnection sub(MazeRoom sub) {
      return new MazeRoomConnection(this.left.sub(sub), this.right.sub(sub));
   }

   @Nullable
   public MazeRoom distance(MazeRoomConnection connection) {
      MazeRoom leftDist = connection.left.sub(this.left);
      return leftDist.equals(connection.right.sub(this.right)) ? leftDist : null;
   }

   public MazeRoomConnection normalize() {
      return new MazeRoomConnection(new MazeRoom(new int[this.getDimensions()]), this.right.sub(this.left));
   }

   public boolean has(MazeRoom room) {
      return Objects.equals(this.left, room) || Objects.equals(this.right, room);
   }

   public MazeRoom setValue(MazeRoom value) {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            MazeRoomConnection that = (MazeRoomConnection)o;
            if (this.left != null) {
               if (!this.left.equals(that.left)) {
                  return false;
               }
            } else if (that.left != null) {
               return false;
            }

            return this.right != null ? this.right.equals(that.right) : that.right == null;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.left != null ? this.left.hashCode() : 0);
      result = 31 * result + (this.right != null ? this.right.hashCode() : 0);
      return result;
   }

   public String toString() {
      return "MazeRoomConnection{left=" + this.left + ", right=" + this.right + '}';
   }
}
