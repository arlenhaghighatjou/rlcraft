package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEvent;

public class RandomMobEventTrigger extends MobEventTrigger {
   public int weight = 8;
   public int priority = 1;

   public RandomMobEventTrigger(MobEvent mobEvent) {
      super(mobEvent);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("weight")) {
         this.weight = json.get("weight").getAsInt();
      }

      if (json.has("priority")) {
         this.priority = json.get("priority").getAsInt();
      }

      super.loadFromJSON(json);
   }
}
