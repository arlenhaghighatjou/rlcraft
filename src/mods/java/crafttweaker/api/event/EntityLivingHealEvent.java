package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.EntityLivingHealEvent")
@ZenRegister
public interface EntityLivingHealEvent extends ILivingEvent, IEventCancelable {
   @ZenGetter("amount")
   float getAmount();

   @ZenSetter("amount")
   void setAmount(float var1);
}
