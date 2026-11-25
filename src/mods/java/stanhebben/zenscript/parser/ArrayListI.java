package stanhebben.zenscript.parser;

public class ArrayListI {
   private int[] data = new int[8];
   private int size;

   public ArrayListI() {
   }

   public void add(int value) {
      if (this.size == this.data.length) {
         this.expand();
      }

      this.data[this.size++] = value;
   }

   public int get(int index) {
      if (index >= this.size) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return this.data[index];
      }
   }

   public int size() {
      return this.size;
   }

   private void expand() {
      int[] newdata = new int[this.data.length * 2];
      System.arraycopy(this.data, 0, newdata, 0, this.size);
      this.data = newdata;
   }

   public int[] toArray() {
      int[] result = new int[this.size];
      System.arraycopy(this.data, 0, result, 0, this.size);
      return result;
   }
}
