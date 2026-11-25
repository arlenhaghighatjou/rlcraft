package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.CriticalHitEvent")
@ZenRegister
public interface CriticalHitEvent extends IPlayerEvent, IEventHasResult {
   @ZenGetter("target")
   IEntity getTarget();

   @ZenGetter("damageModifier")
   float getDamageModifier();

   @ZenSetter("damageModifier")
   void setDamageModifier(float var1);

   @ZenGetter("oldDamageModifier")
   float getOldDamageModifier();

   @ZenGetter("isVanillaCrit")
   boolean isVanillaCrit();
}
