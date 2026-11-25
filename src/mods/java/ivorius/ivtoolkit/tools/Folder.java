package ivorius.ivtoolkit.tools;

import java.util.Iterator;

public class Folder {
   public Folder() {
   }

   public static <T> T fold(Iterable<T> ts, Folder.Reducer<T> reducer, T initial) {
      for(T t : ts) {
         initial = reducer.reduce(initial, t);
      }

      return initial;
   }

   public static <T> T fold(Iterable<T> ts, Folder.Reducer<T> reducer) {
      Iterator<T> iterator = ts.iterator();

      T initial;
      T t;
      for(initial = (T)iterator.next(); iterator.hasNext(); initial = reducer.reduce(initial, t)) {
         t = (T)iterator.next();
      }

      return initial;
   }

   public interface Reducer<T> {
      T reduce(T var1, T var2);
   }
}
