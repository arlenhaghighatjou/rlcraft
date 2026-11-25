package stanhebben.zenscript.parser;

public class HashMapI<T> {
   private int[] keys = new int[16];
   private T[] values = (T[])(new Object[16]);
   private int[] next = new int[16];
   private int mask = 15;
   private int size = 0;

   public HashMapI() {
      for(int i = 0; i < this.keys.length; ++i) {
         this.keys[i] = Integer.MIN_VALUE;
      }

   }

   public void put(int key, T value) {
      if (this.size > this.keys.length * 3 >> 2) {
         this.expand();
      }

      int index = key & this.mask;
      if (this.keys[index] == Integer.MIN_VALUE) {
         this.keys[index] = key;
         this.values[index] = value;
      } else {
         if (this.keys[index] == key) {
            this.values[index] = value;
            return;
         }

         while(this.next[index] != 0) {
            index = this.next[index] - 1;
            if (this.keys[index] == key) {
               this.values[index] = value;
               return;
            }
         }

         int ref = index;

         while(this.keys[index] != Integer.MIN_VALUE) {
            ++index;
            if (index == this.keys.length) {
               index = 0;
            }
         }

         this.next[ref] = index + 1;
         this.keys[index] = key;
         this.values[index] = value;
      }

      ++this.size;
   }

   public T get(int key) {
      int index;
      for(index = key & this.mask; this.keys[index] != key; index = this.next[index] - 1) {
         if (this.next[index] == 0) {
            return (T)null;
         }
      }

      return (T)this.values[index];
   }

   public boolean containsKey(int key) {
      for(int index = key & this.mask; this.keys[index] != key; index = this.next[index] - 1) {
         if (this.next[index] == 0) {
            return false;
         }
      }

      return true;
   }

   public IteratorI keys() {
      return new HashMapI.KeyIterator();
   }

   public int[] keysArray() {
      int[] result = new int[this.size];
      int ix = 0;

      for(int key : this.keys) {
         if (key != Integer.MIN_VALUE) {
            result[ix++] = key;
         }
      }

      return result;
   }

   private void expand() {
      int[] newKeys = new int[this.keys.length * 2];
      T[] newValues = (T[])(new Object[this.values.length * 2]);
      int[] newNext = new int[this.next.length * 2];
      int newMask = newKeys.length - 1;

      for(int i = 0; i < newKeys.length; ++i) {
         newKeys[i] = Integer.MIN_VALUE;
      }

      for(int i = 0; i < this.keys.length; ++i) {
         if (this.keys[i] != Integer.MIN_VALUE) {
            int key = this.keys[i];
            T value = (T)this.values[i];
            int index = key & newMask;
            if (newKeys[index] == Integer.MIN_VALUE) {
               newKeys[index] = key;
               newValues[index] = value;
            } else {
               while(newNext[index] != 0) {
                  index = newNext[index] - 1;
               }

               int ref;
               for(ref = index; newKeys[index] != Integer.MIN_VALUE; index = index + 1 & newMask) {
                  ;
               }

               newNext[ref] = index + 1;
               newKeys[index] = key;
               newValues[index] = value;
            }
         }
      }

      this.keys = newKeys;
      this.values = newValues;
      this.next = newNext;
      this.mask = newMask;
   }

   private class KeyIterator implements IteratorI {
      private int i = 0;

      public KeyIterator() {
         this.skip();
      }

      public boolean hasNext() {
         return this.i < HashMapI.this.keys.length;
      }

      public int next() {
         int result = HashMapI.this.keys[this.i++];
         this.skip();
         return result;
      }

      private void skip() {
         while(this.i < HashMapI.this.keys.length && HashMapI.this.keys[this.i] == Integer.MIN_VALUE) {
            ++this.i;
         }

      }
   }
}
