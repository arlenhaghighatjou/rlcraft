package com.n247s.api.eventapi.eventsystem;

import com.n247s.api.eventapi.EventApi;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class EventBus {
   private static final Logger log = EventApi.logger;
   protected final HashMap<Class<? extends EventType>, CallHandler> EventList = new HashMap();

   public EventBus() {
   }

   public boolean raiseEvent(EventType eventType) {
      try {
         Class eventClass = eventType.getClass();
         if (!this.EventList.containsKey(eventClass)) {
            this.EventList.put(eventClass, new EventApiCallHandler(eventClass));
         }

         return ((CallHandler)this.EventList.get(eventClass)).CallInstances(eventType);
      } catch (Exception var3) {
         log.catching(var3);
         return false;
      }
   }

   public void bindCallHandler(CallHandler callHandler) {
      if (this.EventList.containsKey(callHandler.eventType)) {
         this.EventList.remove(callHandler.eventType);
      }

      this.EventList.put(callHandler.eventType, callHandler);
   }

   public void RegisterEventListener(Object listener) {
      Class clazz;
      if (!(listener instanceof Class)) {
         clazz = listener.getClass();
      } else {
         clazz = (Class)listener;
      }

      Method[] methods = clazz.getMethods();

      for(int i = 0; i < methods.length; ++i) {
         Method currentMethod = methods[i];
         if (currentMethod.isAnnotationPresent(CustomEventSubscribe.class)) {
            if (currentMethod.getParameterTypes().length > 1) {
               log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
            }

            if (!EventType.class.isAssignableFrom(currentMethod.getParameterTypes()[0])) {
               log.catching(new IllegalArgumentException("The Parameter isn't an EventType!"));
            }

            CustomEventSubscribe annotation = (CustomEventSubscribe)currentMethod.getAnnotation(CustomEventSubscribe.class);
            Class<? extends EventType> methodEventType = currentMethod.getParameterTypes()[0];
            this.getCallHandlerFromEventType(methodEventType).RegisterEventListener(annotation.eventPriority(), listener, currentMethod);
         }
      }

   }

   public void RegisterEventListners(List<Object> listenersList) {
      for(int i = 0; i < listenersList.size(); ++i) {
         this.RegisterEventListener(listenersList.get(i));
      }

   }

   public void removeEventListener(Object Listener) {
      Class clazz;
      if (!(Listener instanceof Class)) {
         clazz = Listener.getClass();
      } else {
         clazz = (Class)Listener;
      }

      Method[] methodArray = clazz.getMethods();

      for(int i = 0; i < methodArray.length; ++i) {
         Method currentMethod = methodArray[i];
         if (currentMethod.isAnnotationPresent(CustomEventSubscribe.class)) {
            if (currentMethod.getParameterTypes().length > 1) {
               log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
            }

            if (Listener instanceof Class && !Modifier.isStatic(currentMethod.getModifiers())) {
               log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));
            }

            if (!EventType.class.isAssignableFrom(currentMethod.getParameterTypes()[0])) {
               log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
            }

            CustomEventSubscribe annotation = (CustomEventSubscribe)currentMethod.getAnnotation(CustomEventSubscribe.class);
            Class<? extends EventType> methodEventType = currentMethod.getParameterTypes()[0];
            this.getCallHandlerFromEventType(methodEventType).removeListener(annotation.eventPriority(), Listener);
         }
      }

   }

   public void removeEventListeners(List<Object> listenersList) {
      for(int i = 0; i < listenersList.size(); ++i) {
         this.removeEventListener(listenersList.get(i));
      }

   }

   public CallHandler getCallHandlerFromEventType(Class<? extends EventType> eventTypeClass) {
      if (!this.EventList.containsKey(eventTypeClass)) {
         this.EventList.put(eventTypeClass, new EventApiCallHandler(eventTypeClass));
      }

      return (CallHandler)this.EventList.get(eventTypeClass);
   }
}
