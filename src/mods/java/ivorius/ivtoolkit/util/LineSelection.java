package ivorius.ivtoolkit.util;

import gnu.trove.TIntCollection;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import ivorius.ivtoolkit.gui.IntegerRange;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LineSelection {
   @Nonnull
   private final TIntList cuts = new TIntArrayList();
   private boolean startsAdditive;

   public LineSelection(boolean additive) {
      this.startsAdditive = additive;
   }

   public LineSelection(boolean startsAdditive, TIntCollection cuts) {
      this.startsAdditive = startsAdditive;
      this.cuts.addAll(cuts);
      this.cuts.sort();
   }

   public static LineSelection fromRange(IntegerRange range, boolean additive) {
      return new LineSelection(!additive, new TIntArrayList(new int[]{range.getMin(), range.getMax() + 1}));
   }

   public LineSelection copy() {
      return new LineSelection(this.startsAdditive, this.cuts);
   }

   public void invert() {
      this.startsAdditive = !this.startsAdditive;
   }

   public LineSelection inverted() {
      LineSelection copy = this.copy();
      copy.invert();
      return copy;
   }

   public void set(LineSelection selection) {
      this.cuts.clear();
      this.cuts.addAll(selection.cuts);
      this.startsAdditive = selection.startsAdditive;
   }

   public void set(LineSelection selection, boolean additive, boolean setAdditive) {
      selection.streamSections((IntegerRange)null, additive).forEach((range) -> {
         this.setSection(range, setAdditive);
      });
   }

   public void setSection(@Nullable IntegerRange range, boolean additive) {
      if (range == null) {
         this.cuts.clear();
         this.startsAdditive = additive;
      } else {
         int min = range.min;
         int max = range.max;
         int minSection = this.sectionForIndex(min);
         int maxSection = this.sectionForIndex(max);
         Boolean minAdditive = min == Integer.MIN_VALUE ? null : this.isSectionAdditive(this.sectionForIndex(min - 1));
         Boolean maxAdditive = max == Integer.MAX_VALUE ? null : this.isSectionAdditive(this.sectionForIndex(max + 1));
         this.cuts.remove(minSection, maxSection - minSection);
         if (minAdditive != null) {
            this.ensureLeftCut(min, minAdditive.booleanValue() != additive);
         } else {
            this.startsAdditive = additive;
         }

         if (maxAdditive != null) {
            this.ensureLeftCut(max + 1, additive != maxAdditive.booleanValue());
         }

      }
   }

   private void ensureLeftCut(int index, boolean needsCut) {
      int leftCutIndex = this.sectionForIndex(index) - 1;
      if (needsCut) {
         if (!this.hasCutLeft(leftCutIndex, index)) {
            this.cuts.insert(leftCutIndex + 1, index);
         }
      } else if (this.hasCutLeft(leftCutIndex, index)) {
         this.cuts.remove(leftCutIndex, 1);
      }

   }

   private boolean hasCutLeft(int cutIndex, int index) {
      return cutIndex >= 0 && this.cuts.get(cutIndex) == index;
   }

   public IntStream streamElements(@Nullable IntegerRange range, boolean additive) {
      return this.streamSections(range, additive).flatMapToInt((r) -> {
         return IntStream.range(r.min, r.max + 1);
      });
   }

   public Stream<IntegerRange> streamSections(@Nullable IntegerRange range, boolean additive) {
      int minSection = range == null ? 0 : this.sectionForIndex(range.min);
      int maxSection = range == null ? this.sectionCount() - 1 : this.sectionForIndex(range.max);
      if (this.isSectionAdditive(minSection) != additive) {
         ++minSection;
      }

      return IntStream.range(0, (maxSection - minSection + 2) / 2).mapToObj((i) -> {
         return this.sectionRange(finalMinSection + i * 2);
      });
   }

   public IntegerRange sectionRange(int section) {
      this.assertHasSection(section);
      return new IntegerRange(section > 0 ? this.cuts.get(section - 1) : Integer.MIN_VALUE, section < this.cuts.size() ? this.cuts.get(section) - 1 : Integer.MAX_VALUE);
   }

   public int sectionCount() {
      return this.cuts.size() + 1;
   }

   public boolean isUniform() {
      return this.cuts.isEmpty();
   }

   public boolean isUniform(boolean additive) {
      return this.cuts.isEmpty() && this.startsAdditive == additive;
   }

   public int sectionForIndex(int index) {
      if (index == Integer.MIN_VALUE) {
         return 0;
      } else if (index == Integer.MAX_VALUE) {
         return this.sectionCount() - 1;
      } else {
         int res = this.cuts.binarySearch(index);
         return Math.abs(res + 1);
      }
   }

   public boolean isSectionAdditive(int section) {
      this.assertHasSection(section);
      return (this.startsAdditive ? section : section + 1) % 2 == 0;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LineSelection that = (LineSelection)o;
         return this.startsAdditive != that.startsAdditive ? false : this.cuts.equals(that.cuts);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.cuts.hashCode();
      result = 31 * result + (this.startsAdditive ? 1 : 0);
      return result;
   }

   public String toString() {
      return "LineSelection{cuts=" + this.cuts + ", startsAdditive=" + this.startsAdditive + '}';
   }

   private void assertHasSection(int section) {
      if (section < 0 || section >= this.sectionCount()) {
         throw new ArrayIndexOutOfBoundsException(String.format("section: %d, size: %d", section, this.sectionCount()));
      }
   }
}
