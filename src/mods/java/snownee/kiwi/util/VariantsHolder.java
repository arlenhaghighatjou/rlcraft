package snownee.kiwi.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.util.IStringSerializable;

public class VariantsHolder<T extends IStringSerializable> extends AbstractList<VariantsHolder.Variant<T>> {
   private final List<VariantsHolder.Variant<T>> holder = new ArrayList();

   public VariantsHolder() {
   }

   public VariantsHolder.Variant<T> addVariant(T value) {
      VariantsHolder.Variant<T> variant = new VariantsHolder.Variant<T>(value, this.holder.size());
      this.holder.add(variant);
      return variant;
   }

   public VariantsHolder<T> addVariants(T[] values) {
      for(T value : values) {
         this.holder.add(new VariantsHolder.Variant(value, this.holder.size()));
      }

      return this;
   }

   public int size() {
      return this.holder.size();
   }

   public boolean isEmpty() {
      return this.holder.isEmpty();
   }

   public boolean contains(Object o) {
      return this.holder.contains(o);
   }

   public Iterator<VariantsHolder.Variant<T>> iterator() {
      return this.holder.iterator();
   }

   public Object[] toArray() {
      return this.holder.toArray();
   }

   public boolean add(VariantsHolder.Variant<T> e) {
      return this.holder.add(e);
   }

   public boolean remove(Object o) {
      return this.holder.remove(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.holder.containsAll(c);
   }

   public boolean addAll(Collection<? extends VariantsHolder.Variant<T>> c) {
      return this.holder.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends VariantsHolder.Variant<T>> c) {
      return this.holder.addAll(index, c);
   }

   public boolean removeAll(Collection<?> c) {
      return this.holder.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return this.holder.retainAll(c);
   }

   public void clear() {
      this.holder.clear();
   }

   public VariantsHolder.Variant<T> get(int index) {
      return (VariantsHolder.Variant)this.holder.get(index);
   }

   public VariantsHolder.Variant<T> set(int index, VariantsHolder.Variant<T> element) {
      return (VariantsHolder.Variant)this.holder.set(index, element);
   }

   public void add(int index, VariantsHolder.Variant<T> element) {
      this.holder.add(index, element);
   }

   public VariantsHolder.Variant<T> remove(int index) {
      return (VariantsHolder.Variant)this.holder.remove(index);
   }

   public int indexOf(Object o) {
      return this.holder.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.holder.lastIndexOf(o);
   }

   public ListIterator<VariantsHolder.Variant<T>> listIterator() {
      return this.holder.listIterator();
   }

   public ListIterator<VariantsHolder.Variant<T>> listIterator(int index) {
      return this.holder.listIterator(index);
   }

   public List<VariantsHolder.Variant<T>> subList(int fromIndex, int toIndex) {
      return this.holder.subList(fromIndex, toIndex);
   }

   public static class Type implements IStringSerializable {
      private final String name;

      public Type(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class Variant<T extends IStringSerializable> {
      private final T value;
      private final int meta;

      private Variant(T type, int meta) {
         this.value = type;
         this.meta = meta;
      }

      public T getValue() {
         return this.value;
      }

      public int getMeta() {
         return this.meta;
      }
   }
}
