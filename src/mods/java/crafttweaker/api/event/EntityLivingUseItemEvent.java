package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.event.EntityLivingUseItemEvent.All")
public interface EntityLivingUseItemEvent extends IEventCancelable, ILivingEvent {
   @ZenGetter("item")
   IItemStack getItem();

   @ZenGetter
   boolean isPlayer();

   @ZenGetter("player")
   IPlayer getPlayer();

   @ZenGetter("duration")
   int getDuration();

   @ZenSetter("duration")
   void setDuration(int var1);

   @ZenRegister
   @ZenClass("crafttweaker.event.EntityLivingUseItemEvent.Finish")
   public interface Finish extends EntityLivingUseItemEvent {
   }

   @ZenRegister
   @ZenClass("crafttweaker.event.EntityLivingUseItemEvent.Start")
   public interface Start extends EntityLivingUseItemEvent {
   }

   @ZenRegister
   @ZenClass("crafttweaker.event.EntityLivingUseItemEvent.Stop")
   public interface Stop extends EntityLivingUseItemEvent {
   }

   @ZenRegister
   @ZenClass("crafttweaker.event.EntityLivingUseItemEvent.Tick")
   public interface Tick extends EntityLivingUseItemEvent {
   }
}
