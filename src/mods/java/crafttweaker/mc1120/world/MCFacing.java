package crafttweaker.mc1120.world;

import crafttweaker.api.world.IFacing;
import net.minecraft.util.EnumFacing;

public class MCFacing implements IFacing {
   private final EnumFacing facing;

   public MCFacing(EnumFacing facing) {
      this.facing = facing;
   }

   public IFacing opposite() {
      return new MCFacing(this.facing.getOpposite());
   }

   public Object getInternal() {
      return this.facing;
   }

   public String getName() {
      return this.facing.name();
   }

   public int compare(IFacing other) {
      return this.facing.compareTo((EnumFacing)other.getInternal());
   }

   public IFacing rotateY() {
      return new MCFacing(this.facing.rotateY());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCFacing mcFacing = (MCFacing)o;
         return this.facing == mcFacing.facing;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hash = 5;
      hash = 23 * hash + (this.facing != null ? this.facing.hashCode() : 0);
      return hash;
   }
}
