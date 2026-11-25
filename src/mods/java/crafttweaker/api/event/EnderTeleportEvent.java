package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.event.EnderTeleportEvent")
public interface EnderTeleportEvent extends IEventCancelable, ILivingEvent {
   @ZenGetter("targetX")
   double getTargetX();

   @ZenSetter("targetX")
   void setTargetX(double var1);

   @ZenGetter("targetY")
   double getTargetY();

   @ZenSetter("targetY")
   void setTargetY(double var1);

   @ZenGetter("targetZ")
   double getTargetZ();

   @ZenSetter("targetZ")
   void setTargetZ(double var1);

   @ZenGetter("attackDamage")
   float getAttackDamage();

   @ZenSetter("attackDamage")
   void setAttackDamage(float var1);
}
