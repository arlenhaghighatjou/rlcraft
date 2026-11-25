package com.n247s.api.eventapi;

import com.n247s.api.eventapi.eventsystem.EventBus;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class EventApi {
   public static final Logger logger = LogManager.getLogger("EventApi");
   public static final EventBus defaultEventBusInstance = new EventBus();

   public EventApi() {
   }
}
