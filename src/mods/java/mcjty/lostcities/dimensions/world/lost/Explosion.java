package mcjty.lostcities.dimensions.world.lost;

import mcjty.lostcities.api.ILostExplosion;
import net.minecraft.util.math.BlockPos;

public class Explosion implements ILostExplosion {
   private final int radius;
   private final int sqradius;
   private final BlockPos center;

   public Explosion(int radius, BlockPos center) {
      this.radius = radius;
      this.center = center;
      this.sqradius = radius * radius;
   }

   public int getRadius() {
      return this.radius;
   }

   public int getSqradius() {
      return this.sqradius;
   }

   public BlockPos getCenter() {
      return this.center;
   }
}
