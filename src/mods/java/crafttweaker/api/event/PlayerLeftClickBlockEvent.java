package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IVector3d;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.PlayerLeftClickBlockEvent")
@ZenRegister
public interface PlayerLeftClickBlockEvent extends PlayerInteractEvent, IEventCancelable, IHasCancellationResult {
   @ZenGetter("hitVector")
   IVector3d getHitVector();

   @ZenGetter("useBlock")
   String getUseBlock();

   @ZenSetter("useBlock")
   void setUseBlock(String var1);

   @ZenGetter("useItem")
   String getUseItem();

   @ZenSetter("useItem")
   void setUseItem(String var1);
}
