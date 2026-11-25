package org.orecruncher.lib;

public abstract class Singleton<T> {
   private T instance;

   public Singleton() {
   }

   protected abstract T initialValue();

   public final T get() {
      if (this.instance == null) {
         this.instance = (T)this.initialValue();
      }

      return this.instance;
   }

   public final void clear() {
      synchronized(this) {
         this.instance = null;
      }
   }
}
