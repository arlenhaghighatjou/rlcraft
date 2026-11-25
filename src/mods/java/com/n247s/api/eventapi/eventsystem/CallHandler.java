package com.n247s.api.eventapi.eventsystem;

import com.n247s.api.eventapi.EventApi;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public abstract class CallHandler {
   private static final Logger log = EventApi.logger;
   protected final List<Object> entryCheckList = new ArrayList();
   protected final HashMap<CustomEventSubscribe.Priority, LinkedHashMap> instanceMap = new HashMap();
   protected final Class<? extends EventType> eventType;

   public CallHandler(Class<? extends EventType> eventType) {
      this.eventType = eventType;
      this.instanceMap.put(CustomEventSubscribe.Priority.Highest, new LinkedHashMap());
      this.instanceMap.put(CustomEventSubscribe.Priority.High, new LinkedHashMap());
      this.instanceMap.put(CustomEventSubscribe.Priority.Normal, new LinkedHashMap());
      this.instanceMap.put(CustomEventSubscribe.Priority.Low, new LinkedHashMap());
      this.instanceMap.put(CustomEventSubscribe.Priority.Lowest, new LinkedHashMap());
   }

   protected abstract boolean CallInstances(EventType var1);

   protected final void RegisterEventListener(CustomEventSubscribe.Priority priority, Object Listener, Method method) {
      ((LinkedHashMap)this.instanceMap.get(priority)).put(Listener, method);
      this.entryCheckList.add(Listener);
   }

   protected final void removeListener(CustomEventSubscribe.Priority priority, Object Listener) {
      LinkedHashMap currenInstanceMap = (LinkedHashMap)this.instanceMap.get(priority);
      if (currenInstanceMap == null) {
         log.catching(new NullPointerException("There is no Listner registered for this priority level " + priority.toString() + "!"));
      }

      if (currenInstanceMap.containsKey(Listener)) {
         currenInstanceMap.remove(Listener);
      } else {
         log.catching(new NullPointerException("Current Listner has either changed its annotation value's or is not registered!"));
      }

   }
}
