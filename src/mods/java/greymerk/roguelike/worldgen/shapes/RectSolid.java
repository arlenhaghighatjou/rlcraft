package greymerk.roguelike.worldgen.shapes;

import com.google.common.collect.Sets;
import greymerk.roguelike.worldgen.Bounded;
import greymerk.roguelike.worldgen.Coord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public class RectSolid implements IShape, Bounded {
   private final Coord start;
   private final Coord end;

   private RectSolid(Coord start, Coord end) {
      this.start = start.copy();
      this.end = end.copy();
   }

   public static RectSolid newRect(Coord start, Coord end) {
      return new RectSolid(start, end);
   }

   public RectSolid withHeight(int height) {
      Coord lower = this.start.getY() < this.end.getY() ? this.start : this.end;
      this.start.setY(lower.getY());
      this.end.setY(lower.getY() + Math.max(0, height - 1));
      return this;
   }

   public List<Coord> get() {
      List<Coord> coords = new ArrayList();

      for(Coord c : this) {
         coords.add(c);
      }

      return coords;
   }

   public Set<Coord> getAnchors() {
      return Sets.newHashSet(new Coord[]{this.start, this.end});
   }

   @Nonnull
   public Iterator<Coord> iterator() {
      return new RectSolid.RectSolidIterator(this.start, this.end);
   }

   public IShape getShape(Shape type) {
      return this;
   }

   public Coord getStart() {
      return this.start;
   }

   public Coord getEnd() {
      return this.end;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RectSolid)) {
         return false;
      } else {
         RectSolid other = (RectSolid)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$start = this.getStart();
            Object other$start = other.getStart();
            if (this$start == null) {
               if (other$start != null) {
                  return false;
               }
            } else if (!this$start.equals(other$start)) {
               return false;
            }

            Object this$end = this.getEnd();
            Object other$end = other.getEnd();
            if (this$end == null) {
               if (other$end != null) {
                  return false;
               }
            } else if (!this$end.equals(other$end)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof RectSolid;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $start = this.getStart();
      result = result * 59 + ($start == null ? 43 : $start.hashCode());
      Object $end = this.getEnd();
      result = result * 59 + ($end == null ? 43 : $end.hashCode());
      return result;
   }

   private static class RectSolidIterator implements Iterator<Coord> {
      Coord cursor;
      Coord start;
      Coord end;

      public RectSolidIterator(Coord start, Coord end) {
         this.start = start.copy();
         this.end = end.copy();
         Coord.correct(this.start, this.end);
         this.cursor = this.start.copy();
      }

      public boolean hasNext() {
         return this.cursor.getY() <= this.end.getY();
      }

      public Coord next() {
         Coord toReturn = this.cursor.copy();
         if (this.cursor.getZ() == this.end.getZ() && this.cursor.getX() == this.end.getX()) {
            this.cursor = new Coord(this.start.getX(), this.cursor.getY(), this.start.getZ());
            this.cursor.up();
            return toReturn;
         } else if (this.cursor.getX() == this.end.getX()) {
            this.cursor = new Coord(this.start.getX(), this.cursor.getY(), this.cursor.getZ());
            this.cursor.south();
            return toReturn;
         } else {
            this.cursor.east();
            return toReturn;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
