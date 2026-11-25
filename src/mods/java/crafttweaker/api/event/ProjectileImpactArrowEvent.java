package crafttweaker.api.event;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityArrow;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.ProjectileImpactArrowEvent")
@ZenRegister
public interface ProjectileImpactArrowEvent extends IProjectileImpactEvent, IEventCancelable {
   @ZenGetter("arrow")
   default IEntityArrow getArrowNew() {
      CraftTweakerAPI.logError("Class " + this.getClass() + " doesn't override ProjectileImpactArrowEvent#getArrowNew");
      return null;
   }

   /** @deprecated */
   @Deprecated
   IEntity getArrow();

   @ZenGetter("shooter")
   IEntity getShooter();

   @ZenGetter("damage")
   double getDamage();

   @ZenSetter("damage")
   void setDamage(double var1);

   @ZenSetter("knockbackStrength")
   void setKnockbackStrength(int var1);

   @ZenGetter("isCritical")
   boolean getIsCritical();

   @ZenSetter("isCritical")
   void setIsCritical(boolean var1);

   @ZenGetter("pickupStatus")
   String getPickupStatus();

   @ZenMethod
   void setPickupDisallowed();

   @ZenMethod
   void setPickupAllowed();

   @ZenMethod
   void setPickupCreativeOnly();
}
