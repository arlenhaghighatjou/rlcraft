package ivorius.ivtoolkit.tools;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.tuple.Pair;

public class Pairs {
   public Pairs() {
   }

   public static <T> Iterable<T> both(Iterable<? extends Pair<T, T>> set) {
      Stream var10000 = StreamSupport.stream(set.spliterator(), false);
      Function var10001 = leftFunction();
      var10001.getClass();
      Iterable var1 = (Iterable)var10000.map(var10001::apply).collect(Collectors.toList());
      Stream var2 = StreamSupport.stream(set.spliterator(), false);
      Function var10002 = rightFunction();
      var10002.getClass();
      return Sets.newHashSet(Iterables.concat(var1, (Iterable)var2.map(var10002::apply).collect(Collectors.toList())));
   }

   public static <T> Function<Pair<T, T>, T> rightFunction() {
      return (input) -> {
         return (Function<Pair<T, T>, T>)input.getRight();
      };
   }

   public static <T> Function<Pair<T, T>, T> leftFunction() {
      return (input) -> {
         return (Function<Pair<T, T>, T>)input.getLeft();
      };
   }

   public static <L, R> Iterable<Pair<L, R>> pairLeft(L left, Iterable<R> right) {
      return (Iterable)StreamSupport.stream(right.spliterator(), false).map((input) -> {
         return Pair.of(left, input);
      }).collect(Collectors.toList());
   }

   public static <L, R> Iterable<Pair<L, R>> pairRight(Iterable<L> left, R right) {
      return (Iterable)StreamSupport.stream(left.spliterator(), false).map((input) -> {
         return Pair.of(input, right);
      }).collect(Collectors.toList());
   }

   public static <L, R> Iterable<Pair<L, R>> of(final Iterable<L> left, final Iterable<R> right) {
      return new FluentIterable<Pair<L, R>>() {
         public Iterator<Pair<L, R>> iterator() {
            return new Pairs.PairIterator(left.iterator(), right.iterator());
         }
      };
   }

   public static <L, R> List<Pair<L, R>> of(List<L> left, List<R> right) {
      return new Pairs.PairList(left, right);
   }

   protected static class PairIterator<L, R> implements Iterator<Pair<L, R>> {
      protected Iterator<L> left;
      protected Iterator<R> right;

      public PairIterator(Iterator<L> left, Iterator<R> right) {
         this.left = left;
         this.right = right;
      }

      public boolean hasNext() {
         return this.left.hasNext() && this.right.hasNext();
      }

      public Pair<L, R> next() {
         return Pair.of(this.left.next(), this.right.next());
      }

      public void remove() {
         this.left.remove();
         this.right.remove();
      }
   }

   protected static class PairList<L, R> extends AbstractList<Pair<L, R>> {
      protected List<L> left;
      protected List<R> right;

      public PairList(List<L> left, List<R> right) {
         this.left = left;
         this.right = right;
      }

      public Pair<L, R> get(int index) {
         return Pair.of(this.left.get(index), this.right.get(index));
      }

      public int size() {
         return Math.min(this.left.size(), this.right.size());
      }

      public Iterator<Pair<L, R>> iterator() {
         return new Pairs.PairIterator(this.left.listIterator(), this.right.listIterator());
      }

      public boolean remove(Object o) {
         return this.left.remove(((Pair)o).getLeft()) && this.right.remove(((Pair)o).getRight());
      }
   }
}
