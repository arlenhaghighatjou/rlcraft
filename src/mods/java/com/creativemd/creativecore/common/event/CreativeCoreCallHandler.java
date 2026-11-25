package com.creativemd.creativecore.common.event;

import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import com.n247s.api.eventapi.eventsystem.EventApiCallHandler;
import com.n247s.api.eventapi.eventsystem.EventType;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CreativeCoreCallHandler extends EventApiCallHandler {
   public CreativeCoreCallHandler(Class<? extends EventType> eventType) {
      super(eventType);
   }

   protected boolean CallInstances(EventType eventType) {
      return super.CallInstances(eventType);
   }

   public Class<? extends EventType> getEventType() {
      return this.eventType;
   }

   public HashMap<CustomEventSubscribe.Priority, LinkedHashMap> getInstanceMap() {
      return this.instanceMap;
   }
}
