package com.fantasticsource.tools.datastructures;

public class WrappingQueue<T> {
   private Object[] array;
   private int insertPos = 0;
   private int startPos = 0;
   private int used = 0;

   public WrappingQueue(int capacity) {
      this.array = new Object[capacity];
   }

   public boolean add(T t) {
      this.array[this.insertPos] = t;
      if (++this.insertPos == this.array.length) {
         this.insertPos = 0;
      }

      if (++this.used > this.array.length) {
         this.used = this.array.length;
         if (++this.startPos == this.array.length) {
            this.startPos = 0;
         }

         return true;
      } else {
         return false;
      }
   }

   public T getOldestToNewest(int index) {
      if (index >= 0 && index < this.used) {
         return (T)this.array[(index + this.startPos) % this.array.length];
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + this.used);
      }
   }

   public void setOldestToNewest(int index, T value) {
      if (index >= 0 && index < this.used) {
         this.array[(index + this.startPos) % this.array.length] = value;
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + this.used);
      }
   }

   public T getNewestToOldest(int index) {
      if (index > -this.used && index <= 0) {
         return (T)this.array[(index + this.insertPos - 1 + this.array.length) % this.array.length];
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + this.used);
      }
   }

   public void setNewestToOldest(int index, T value) {
      if (index > -this.used && index <= 0) {
         this.array[(index + this.insertPos - 1 + this.array.length) % this.array.length] = value;
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + this.used);
      }
   }

   public T pop() {
      return (T)this.remove(0);
   }

   public T remove(int index) {
      if (index >= 0 && index < this.used) {
         int i = (index + this.startPos) % this.array.length;

         T result;
         for(result = (T)this.array[i]; index >= 0; --index) {
            if (i == 0) {
               this.array[0] = this.array[this.array.length - 1];
            } else {
               this.array[i] = this.array[i - 1];
            }

            --i;
            if (i < 0) {
               i = this.array.length - 1;
            }
         }

         --this.used;
         ++this.startPos;
         if (this.startPos == this.array.length) {
            this.startPos = 0;
         }

         return result;
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + this.used);
      }
   }

   public boolean remove(T t) {
      int index = this.indexOf(t);
      if (index == -1) {
         return false;
      } else {
         this.remove(index);
         return true;
      }
   }

   public Object[] toArray() {
      return this.toArray(0, this.used);
   }

   public Object[] toArray(int index, int length) {
      if (index >= 0 && length >= 0 && index + length <= this.used) {
         Object[] result = new Object[length];
         index = (index + this.startPos) % this.array.length;

         for(int i = 0; i < length; ++i) {
            result[i] = this.array[index];
            ++index;
            if (index == this.array.length) {
               index = 0;
            }
         }

         return result;
      } else {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + length + ", Length: " + this.used);
      }
   }

   public int size() {
      return this.used;
   }

   public int indexOf(T t) {
      int index = this.startPos;

      for(int i = 0; i < this.used; ++i) {
         if (this.array[index].equals(t)) {
            return i;
         }

         ++index;
         if (index == this.array.length) {
            index = 0;
         }
      }

      return -1;
   }

   public boolean contains(T t) {
      return this.indexOf(t) != -1;
   }

   public void clear() {
      this.startPos = 0;
      this.used = 0;
      this.insertPos = 0;
   }
}
