package stanhebben.zenscript.parser;

import java.util.Arrays;

public class HashSetI {
   private int[] values;
   private int[] next;
   private int mask;
   private int size;

   public HashSetI() {
      this.values = new int[16];
      this.next = new int[16];
      this.mask = 15;
      this.size = 0;

      for(int i = 0; i < this.values.length; ++i) {
         this.values[i] = Integer.MIN_VALUE;
      }

   }

   public HashSetI(HashSetI original) {
      this.values = Arrays.copyOf(original.values, original.values.length);
      this.next = Arrays.copyOf(original.next, original.next.length);
      this.mask = original.mask;
      this.size = original.size;
   }

   public void add(int value) {
      if (this.size > this.values.length * 3 >> 2) {
         this.expand();
      }

      int index = value & this.mask;
      if (this.values[index] == Integer.MIN_VALUE) {
         this.values[index] = value;
      } else {
         if (this.values[index] == value) {
            return;
         }

         while(this.next[index] != 0) {
            index = this.next[index] - 1;
            if (this.values[index] == value) {
               return;
            }
         }

         int ref = index;

         while(this.values[index] != Integer.MIN_VALUE) {
            ++index;
            if (index == this.values.length) {
               index = 0;
            }
         }

         this.next[ref] = index + 1;
         this.values[index] = value;
      }

      ++this.size;
   }

   public boolean contains(int value) {
      for(int index = value & this.mask; this.values[index] != value; index = this.next[index] - 1) {
         if (this.next[index] == 0) {
            return false;
         }
      }

      return true;
   }

   public IteratorI iterator() {
      return new HashSetI.KeyIterator();
   }

   public int[] toArray() {
      int[] result = new int[this.size];
      int ix = 0;

      for(int value : this.values) {
         if (value != Integer.MIN_VALUE) {
            result[ix++] = value;
         }
      }

      return result;
   }

   private void expand() {
      int[] newKeys = new int[this.values.length * 2];
      int[] newNext = new int[this.next.length * 2];
      int newMask = newKeys.length - 1;

      for(int i = 0; i < newKeys.length; ++i) {
         newKeys[i] = Integer.MIN_VALUE;
      }

      for(int value : this.values) {
         if (value != Integer.MIN_VALUE) {
            int index = value & newMask;
            if (newKeys[index] == Integer.MIN_VALUE) {
               newKeys[index] = value;
            } else {
               while(newNext[index] != 0) {
                  index = newNext[index] - 1;
               }

               int ref;
               for(ref = index; newKeys[index] != Integer.MIN_VALUE; index = index + 1 & newMask) {
                  ;
               }

               newNext[ref] = index + 1;
               newKeys[index] = value;
            }
         }
      }

      this.values = newKeys;
      this.next = newNext;
      this.mask = newMask;
   }

   private class KeyIterator implements IteratorI {
      private int i = 0;

      public KeyIterator() {
         this.skip();
      }

      public boolean hasNext() {
         return this.i < HashSetI.this.values.length;
      }

      public int next() {
         int result = HashSetI.this.values[this.i++];
         this.skip();
         return result;
      }

      private void skip() {
         while(this.i < HashSetI.this.values.length && HashSetI.this.values[this.i] == Integer.MIN_VALUE) {
            ++this.i;
         }

      }
   }
}
