package com.Shultrea.Rin.Utility_Sector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TaggedList<E, T> extends ArrayList<E> {
   Map<E, Set<T>> tags = new HashMap();

   public TaggedList() {
   }

   public boolean add(E e, T tag) {
      if (!this.tags.containsKey(e)) {
         this.tags.put(e, new HashSet());
      }

      ((Set)this.tags.get(e)).add(tag);
      return super.add(e);
   }

   public boolean add(E e, Collection<? extends T> taglst) {
      if (!this.tags.containsKey(e)) {
         this.tags.put(e, new HashSet());
      }

      ((Set)this.tags.get(e)).addAll(taglst);
      return super.add(e);
   }

   public Set<T> getTags(E e) {
      Set<T> ret = (Set)this.tags.get(e);
      if (ret == null && this.contains(e)) {
         this.tags.put(e, new HashSet());
         ret = (Set)this.tags.get(e);
      }

      return ret;
   }

   public Set<T> getTags(int index) {
      return this.getTags(this.get(index));
   }

   public void addTag(E e, T tag) {
      if (this.contains(e) && !this.tags.containsKey(e)) {
         this.tags.put(e, new HashSet());
      }

      ((Set)this.tags.get(e)).add(tag);
   }

   public void addTag(int index, T tag) {
      this.addTag(this.get(index), tag);
   }

   public void removeTag(E e, T tag) {
      if (this.contains(e) && !this.tags.containsKey(e)) {
         this.tags.put(e, new HashSet());
      }

      ((Set)this.tags.get(e)).remove(tag);
   }

   public void removeTag(int index, T tag) {
      this.removeTag(this.get(index), tag);
   }

   public Set<E> getEntries(T tag) {
      Set<E> ret = new HashSet();

      for(Entry<E, Set<T>> s : this.tags.entrySet()) {
         if (((Set)s.getValue()).contains(tag)) {
            ret.add(s.getKey());
         }
      }

      return ret;
   }

   public void removeEntries(T tag) {
      for(E e : this.getEntries(tag)) {
         this.remove(e);
      }

   }

   public String getTagsAsString(E e) {
      String ret = "";

      for(T s : (Set<T>)this.tags.get(e)) {
         ret = ret + s.toString() + ",";
      }

      if (ret.length() > 0) {
         ret = ret.substring(0, ret.length() - 1);
      }

      return ret;
   }

   public void clear() {
      this.tags.clear();
      super.clear();
   }

   public E set(int index, E element) {
      this.tags.remove(this.get(index));
      return (E)super.set(index, element);
   }

   public E remove(int index) {
      this.tags.remove(this.get(index));
      return (E)super.remove(index);
   }

   public boolean remove(Object o) {
      this.tags.remove(o);
      return super.remove(o);
   }

   public boolean removeAll(Collection<?> c) {
      for(Object o : c) {
         this.tags.remove(o);
      }

      return super.removeAll(c);
   }

   protected void removeRange(int fromIndex, int toIndex) {
      for(int i = fromIndex; i < toIndex; ++i) {
         this.tags.remove(this.get(i));
      }

      super.removeRange(fromIndex, toIndex);
   }

   public boolean retainAll(Collection<?> c) {
      for(E e : this.tags.keySet()) {
         if (!c.contains(e)) {
            this.tags.remove(e);
         }
      }

      return super.retainAll(c);
   }
}
