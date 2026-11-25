package com.n247s.api.eventapi;

import com.n247s.api.eventapi.eventsystem.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventApi {
   public static final Logger logger = LogManager.getLogger("EventApi");
   public static final EventBus defaultEventBusInstance = new EventBus();

   public EventApi() {
   }
}
