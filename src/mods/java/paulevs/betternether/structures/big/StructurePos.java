package paulevs.betternether.structures.big;

import net.minecraft.util.math.Vec3i;

public class StructurePos extends Vec3i {
   protected static final int SIDE = 1290;

   public StructurePos(int x, int y, int z) {
      super(x, y, z);
   }

   public int hashCode() {
      return (this.getX() * 1290 + this.getY()) * 1290 + this.getZ();
   }
}
