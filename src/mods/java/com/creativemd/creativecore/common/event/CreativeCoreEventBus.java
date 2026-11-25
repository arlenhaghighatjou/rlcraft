package com.creativemd.creativecore.common.event;

import com.creativemd.creativecore.CreativeCore;
import com.n247s.api.eventapi.eventsystem.CallHandler;
import com.n247s.api.eventapi.eventsystem.EventBus;
import com.n247s.api.eventapi.eventsystem.EventType;
import java.util.ArrayList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeCoreEventBus extends EventBus {
   public ArrayList<EventType> eventsToRaise = new ArrayList();
   private boolean isClient;

   public CreativeCoreEventBus(boolean isClient) {
      this.isClient = isClient;
      if (isClient) {
         this.initClient();
      } else {
         this.initServer();
      }

   }

   public void initServer() {
      if (CreativeTickHandler.ServerEvents != null) {
         CreativeTickHandler.ServerEvents.add(this);
      }

   }

   @SideOnly(Side.CLIENT)
   public void initClient() {
      if (CreativeTickHandler.ClientEvents != null) {
         CreativeTickHandler.ClientEvents.add(this);
      }

   }

   public boolean raiseEvent(EventType event, boolean force) {
      if (!event.isCancelable() && !force) {
         this.eventsToRaise.add(event);
         return false;
      } else {
         if (!this.EventList.containsKey(event.getClass())) {
            this.EventList.put(event.getClass(), new CreativeCoreCallHandler(event.getClass()));
         }

         return ((CreativeCoreCallHandler)this.EventList.get(event.getClass())).CallInstances(event);
      }
   }

   public boolean raiseEvent(EventType eventType) {
      try {
         Class eventClass = eventType.getClass();
         if (!this.EventList.containsKey(eventClass)) {
            this.EventList.put(eventClass, new CreativeCoreCallHandler(eventClass));
         }

         return ((CreativeCoreCallHandler)this.EventList.get(eventClass)).CallInstances(eventType);
      } catch (Exception var3) {
         CreativeCore.LOGGER.catching(var3);
         return false;
      }
   }

   public void bindCreativeCoreCallHandler(CreativeCoreCallHandler callHandler) {
      if (this.EventList.containsKey(callHandler.getEventType())) {
         this.EventList.remove(callHandler.getEventType());
      }

      this.EventList.put(callHandler.getEventType(), callHandler);
   }

   public void removeAllEventListeners() {
      for(Object value : this.EventList.values()) {
         ((CreativeCoreCallHandler)value).getInstanceMap().clear();
      }

      this.EventList.clear();
      if (this.isClient) {
         this.removeTickEventClient();
      } else {
         this.removeTickEventServer();
      }

   }

   public void removeTickEventServer() {
      CreativeTickHandler.ServerEvents.remove(this);
   }

   @SideOnly(Side.CLIENT)
   public void removeTickEventClient() {
      CreativeTickHandler.ClientEvents.remove(this);
   }

   public CallHandler getCallHandlerFromEventType(Class<? extends EventType> eventTypeClass) {
      if (!this.EventList.containsKey(eventTypeClass)) {
         this.EventList.put(eventTypeClass, new CreativeCoreCallHandler(eventTypeClass));
      }

      return (CallHandler)this.EventList.get(eventTypeClass);
   }
}
