package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityArrow")
@ZenRegister
public interface IEntityArrow extends IEntity, IProjectile {
   @ZenMethod
   @ZenSetter("damage")
   void setDamage(double var1);

   @ZenMethod
   @ZenGetter("damage")
   double getDamage();

   @ZenMethod
   @ZenSetter("isCritical")
   void setIsCritical(boolean var1);

   @ZenMethod
   @ZenGetter("isCritical")
   boolean getIsCritical();

   @ZenMethod
   @ZenGetter("knockbackStrength")
   int getKnockbackStrength();

   @ZenMethod
   @ZenSetter("knockbackStrength")
   void setKnockbackStrength(int var1);

   @ZenMethod
   @ZenGetter("shake")
   int arrowShake();

   @ZenMethod
   @ZenSetter("shooter")
   void setShooter(IEntity var1);

   @ZenMethod
   @ZenGetter("shooter")
   IEntity getShooter();

   @ZenMethod
   @ZenGetter("pickupStatus")
   String getPickupStatus();

   @ZenMethod
   @ZenSetter("pickupStatus")
   void setPickupStatus(String var1);

   @ZenMethod
   void setPickupDisallowed();

   @ZenMethod
   void setPickupAllowed();

   @ZenMethod
   void setPickupCreativeOnly();

   @ZenMethod
   void shoot(IEntity var1, float var2, float var3, float var4, float var5, float var6);
}
