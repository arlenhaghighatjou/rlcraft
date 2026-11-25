package crafttweaker.mc1120.util;

import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.mc1120.world.MCBlockPos;
import java.util.Objects;

public class MCPosition3f implements Position3f {
   private float x;
   private float y;
   private float z;

   public MCPosition3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public void setX(float x) {
      this.x = x;
   }

   public void setY(float y) {
      this.y = y;
   }

   public void setZ(float z) {
      this.z = z;
   }

   public IBlockPos asBlockPos() {
      return new MCBlockPos((int)this.x, (int)this.y, (int)this.z);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCPosition3f that = (MCPosition3f)o;
         return Float.compare(that.x, this.x) == 0 && Float.compare(that.y, this.y) == 0 && Float.compare(that.z, this.z) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.x, this.y, this.z});
   }
}
