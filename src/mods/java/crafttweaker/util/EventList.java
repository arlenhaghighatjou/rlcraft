package crafttweaker.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.event.IEventHandle;

public class EventList<T> {
   private EventList<T>.EventNode first = null;
   private EventList<T>.EventNode last = null;

   public EventList() {
   }

   public void clear() {
      this.first = this.last = null;
   }

   public IEventHandle add(IEventHandler<T> handler) {
      EventList<T>.EventNode node = new EventList.EventNode(handler, this.last, (EventList.EventNode)null);
      synchronized(this) {
         if (this.first == null) {
            this.first = node;
         }

         if (this.last != null) {
            this.last.next = node;
         }

         this.last = node;
         return node;
      }
   }

   public boolean hasHandlers() {
      return this.first != null;
   }

   public boolean isEmpty() {
      return this.first == null;
   }

   public void publish(T event) {
      EventList<T>.EventNode current;
      synchronized(this) {
         current = this.first;
      }

      while(current != null) {
         try {
            current.handler.handle(event);
         } catch (Throwable var7) {
            CraftTweakerAPI.logError(var7.getMessage(), var7);
         }

         synchronized(this) {
            current = current.next;
         }
      }

   }

   private class EventNode implements IEventHandle {
      private final IEventHandler<T> handler;
      private EventList<T>.EventNode next;
      private EventList<T>.EventNode prev;

      public EventNode(IEventHandler<T> handler, EventList<T>.EventNode prev, EventList<T>.EventNode next) {
         this.handler = handler;
         this.prev = prev;
         this.next = next;
      }

      public void close() {
         synchronized(EventList.this) {
            if (this.prev == null) {
               EventList.this.first = this.next;
            } else {
               this.prev.next = this.next;
            }

            if (this.next == null) {
               EventList.this.last = this.prev;
            } else {
               this.next.prev = this.prev;
            }

         }
      }
   }
}
