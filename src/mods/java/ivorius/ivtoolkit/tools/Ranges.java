package ivorius.ivtoolkit.tools;

import java.util.AbstractList;
import java.util.List;

public class Ranges {
   public Ranges() {
   }

   public static List<Integer> rangeList(int start, int end, int stride) {
      return new Ranges.RangeList(start, end, end < start ? -stride : stride);
   }

   public static int[] range(int start, int end, int stride) {
      if (end < start) {
         int[] range = new int[(start - end) / stride];

         for(int i = 0; i < range.length; ++i) {
            range[i] = i * -stride + start;
         }

         return range;
      } else {
         int[] range = new int[(end - start) / stride];

         for(int i = 0; i < range.length; ++i) {
            range[i] = i * stride + start;
         }

         return range;
      }
   }

   public static List<Integer> rangeList(int start, int end) {
      return rangeList(start, end, 1);
   }

   public static int[] range(int start, int end) {
      return range(start, end, 1);
   }

   public static List<Integer> toIterable(int end) {
      return rangeList(0, end, 1);
   }

   public static int[] to(int end) {
      return range(0, end, 1);
   }

   protected static class RangeList extends AbstractList<Integer> {
      protected int start;
      protected int end;
      protected int stride;

      public RangeList(int start, int end, int stride) {
         this.start = start;
         this.end = end;
         this.stride = stride;
      }

      public Integer get(int index) {
         if (index < this.size() && index >= 0) {
            return this.start + this.stride * index;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public int size() {
         return this.stride > 0 ? (this.end - this.start + (this.stride - 1)) / this.stride : (this.end - this.start + this.stride + 1) / this.stride;
      }
   }
}
