package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.EntityLivingDamageEvent")
@ZenRegister
public interface EntityLivingDamageEvent extends ILivingEvent, IEventCancelable {
   @ZenGetter("damageSource")
   IDamageSource getDamageSource();

   @ZenGetter("amount")
   float getAmount();

   @ZenSetter("amount")
   void setAmount(float var1);
}
