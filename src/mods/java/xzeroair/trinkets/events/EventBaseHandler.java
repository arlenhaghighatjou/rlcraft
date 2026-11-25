package xzeroair.trinkets.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class EventBaseHandler {
   public EventBaseHandler() {
   }

   public boolean isEventCanceled(Event event) {
      if (event.isCancelable()) {
         return event.isCanceled();
      } else {
         return true;
      }
   }

   public void cancelEvent(Event event) {
      if (event.isCancelable() && !event.isCanceled()) {
         event.setCanceled(true);
      }

   }

   public void uncancelEvent(Event event) {
      if (event.isCancelable() && event.isCanceled()) {
         event.setCanceled(false);
      }

   }
}
