package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.entity.IEntityThrowable;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.projectile.EntityThrowable;

public class MCEntityThrowable extends MCEntity implements IEntityThrowable {
   private final EntityThrowable entityThrowable;

   public MCEntityThrowable(EntityThrowable entityThrowable) {
      super(entityThrowable);
      this.entityThrowable = entityThrowable;
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      this.entityThrowable.shoot(x, y, z, velocity, inaccuracy);
   }

   public IEntityLivingBase getThrower() {
      return CraftTweakerMC.getIEntityLivingBase(this.entityThrowable.getThrower());
   }

   public int getThrowableShake() {
      return this.entityThrowable.throwableShake;
   }
}
