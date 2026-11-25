package ivorius.reccomplex.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;

public class PrecedenceSet<T> extends AbstractSet<T> implements Comparable<PrecedenceSet> {
   protected float precedence;
   protected Set<T> set;

   public PrecedenceSet(float precedence, Set<T> set) {
      this.precedence = precedence;
      this.set = set;
   }

   public float getPrecedence() {
      return this.precedence;
   }

   public void setPrecedence(float precedence) {
      this.precedence = precedence;
   }

   public Set<T> getSet() {
      return this.set;
   }

   public void setSet(Set<T> set) {
      this.set = set;
   }

   public int compareTo(@Nonnull PrecedenceSet o) {
      return Float.compare(this.precedence, o.precedence);
   }

   @Nonnull
   public Iterator<T> iterator() {
      return this.set.iterator();
   }

   public int size() {
      return this.set.size();
   }

   public interface NativeEntry {
      float getPrecedence();
   }
}
