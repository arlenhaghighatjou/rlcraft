package hunternif.mc.atlas.util;

import java.util.BitSet;

public class BitMatrix {
   private final BitSet set;
   private final int width;
   private final int height;

   private BitMatrix(int width, int height) {
      this.width = width;
      this.height = height;
      this.set = new BitSet(width * height);
   }

   public BitMatrix(int width, int height, boolean initialValue) {
      this(width, height);
      this.set.set(0, this.set.size() - 1, initialValue);
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void set(int x, int y, boolean value) {
      if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
         this.set.set(this.calcIndex(x, y), value);
      }
   }

   public boolean get(int x, int y) {
      return x >= 0 && y >= 0 && x < this.width && y < this.height && this.set.get(this.calcIndex(x, y));
   }

   private int calcIndex(int x, int y) {
      return x + y * this.width;
   }
}
