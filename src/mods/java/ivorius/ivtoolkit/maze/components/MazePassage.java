package ivorius.ivtoolkit.maze.components;

import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class MazePassage extends Pair<MazeRoom, MazeRoom> {
   private final MazeRoom source;
   private final MazeRoom dest;

   public MazePassage(MazeRoom source, MazeRoom dest) {
      this.source = source;
      this.dest = dest;
      if (source.getDimensions() != dest.getDimensions()) {
         throw new IllegalArgumentException();
      }
   }

   public MazeRoom getSource() {
      return this.source;
   }

   public MazeRoom getDest() {
      return this.dest;
   }

   public MazeRoom getLeft() {
      return this.source;
   }

   public MazeRoom getRight() {
      return this.dest;
   }

   public int getDimensions() {
      return this.source.getDimensions();
   }

   public MazePassage add(MazeRoom add) {
      return new MazePassage(this.source.add(add), this.dest.add(add));
   }

   public MazePassage sub(MazeRoom sub) {
      return new MazePassage(this.source.sub(sub), this.dest.sub(sub));
   }

   public MazeRoomConnection toConnection() {
      return new MazeRoomConnection(this.source, this.dest);
   }

   @Nullable
   public MazeRoom distance(MazePassage connection) {
      MazeRoom leftDist = connection.source.sub(this.source);
      return leftDist.equals(connection.dest.sub(this.dest)) ? leftDist : null;
   }

   @Nullable
   public MazeRoom inverseDistance(MazePassage connection) {
      MazeRoom leftDist = connection.source.sub(this.dest);
      return leftDist.equals(connection.dest.sub(this.source)) ? leftDist : null;
   }

   public MazePassage inverse() {
      return new MazePassage(this.dest, this.source);
   }

   public MazePassage normalize() {
      return new MazePassage(new MazeRoom(new int[this.getDimensions()]), this.dest.sub(this.source));
   }

   public boolean has(MazeRoom room) {
      return Objects.equals(this.source, room) || Objects.equals(this.dest, room);
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
            MazePassage that = (MazePassage)o;
            if (this.source != null) {
               if (!this.source.equals(that.source)) {
                  return false;
               }
            } else if (that.source != null) {
               return false;
            }

            return this.dest != null ? this.dest.equals(that.dest) : that.dest == null;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.source != null ? this.source.hashCode() : 0);
      result = 31 * result + (this.dest != null ? this.dest.hashCode() : 0);
      return result;
   }

   public String toString() {
      return "MazePassage{source=" + this.source + ", dest=" + this.dest + '}';
   }
}
