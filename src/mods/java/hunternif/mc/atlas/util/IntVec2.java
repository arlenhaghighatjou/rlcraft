package hunternif.mc.atlas.util;

import net.minecraft.util.math.MathHelper;

public class IntVec2 {
   public int x;
   public int y;

   public IntVec2(IntVec2 vec) {
      this(vec.x, vec.y);
   }

   private IntVec2(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public IntVec2(double x, double y) {
      this.x = MathHelper.floor(x);
      this.y = MathHelper.floor(y);
   }

   public IntVec2 add(int dx, int dy) {
      this.x += dx;
      this.y += dy;
      return this;
   }

   public IntVec2 set(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public String toString() {
      return "(" + this.x + ", " + this.y + ")";
   }

   public IntVec2 clone() {
      return new IntVec2(this.x, this.y);
   }

   public double distanceTo(IntVec2 intVec2) {
      double x1 = (double)this.x;
      double y1 = (double)this.y;
      double x2 = (double)intVec2.x;
      double y2 = (double)intVec2.y;
      return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof IntVec2)) {
         return false;
      } else {
         IntVec2 vec = (IntVec2)obj;
         return vec.x == this.x && vec.y == this.y;
      }
   }

   public int hashCode() {
      return this.x + (this.y << 16);
   }

   public boolean equalsIntVec3(IntVec2 vec) {
      return vec.x == this.x && vec.y == this.y;
   }
}
