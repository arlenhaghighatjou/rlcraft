package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.ProjectileImpactFireballEvent")
@ZenRegister
public interface ProjectileImpactFireballEvent extends IProjectileImpactEvent, IEventCancelable {
   @ZenGetter("fireball")
   IEntity getFireball();

   @ZenGetter("shooter")
   IEntityLivingBase getShooter();

   @ZenGetter("accelerationX")
   double getAccelerationX();

   @ZenSetter("accelerationX")
   void setAccelerationX(double var1);

   @ZenGetter("accelerationY")
   double getAccelerationY();

   @ZenSetter("accelerationY")
   void setAccelerationY(double var1);

   @ZenGetter("accelerationZ")
   double getAccelerationZ();

   @ZenSetter("accelerationZ")
   void setAccelerationZ(double var1);
}
