package crafttweaker.mc1120.world;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IVector3d;
import java.util.Objects;
import net.minecraft.util.math.Vec3d;

public class MCVector3d implements IVector3d {
   private final Vec3d vec;

   public MCVector3d(Vec3d vec) {
      this.vec = vec;
   }

   public double getX() {
      return this.vec.x;
   }

   public double getY() {
      return this.vec.y;
   }

   public double getZ() {
      return this.vec.z;
   }

   public IVector3d getNormalized() {
      return new MCVector3d(this.vec.normalize());
   }

   public double dotProduct(IVector3d other) {
      return this.vec.dotProduct(CraftTweakerMC.getVec3d(other));
   }

   public IVector3d crossProduct(IVector3d other) {
      return CraftTweakerMC.getIVector3d(this.vec.crossProduct(CraftTweakerMC.getVec3d(other)));
   }

   public IVector3d subtract(IVector3d other) {
      return CraftTweakerMC.getIVector3d(this.vec.subtract(CraftTweakerMC.getVec3d(other)));
   }

   public IVector3d subtractReverse(IVector3d other) {
      return CraftTweakerMC.getIVector3d(this.vec.subtractReverse(CraftTweakerMC.getVec3d(other)));
   }

   public IVector3d add(IVector3d other) {
      return CraftTweakerMC.getIVector3d(this.vec.add(CraftTweakerMC.getVec3d(other)));
   }

   public double distanceTo(IVector3d other) {
      return this.vec.distanceTo(CraftTweakerMC.getVec3d(other));
   }

   public IVector3d scale(double factor) {
      return CraftTweakerMC.getIVector3d(this.vec.scale(factor));
   }

   public Vec3d getInternal() {
      return this.vec;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCVector3d that = (MCVector3d)o;
         return Objects.equals(this.vec, that.vec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.vec});
   }
}
