package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.world.World;

public class MobEventSpawnTrigger extends SpawnTrigger {
   public String eventName = "";
   public double eventTime = 0.0D;

   public MobEventSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("eventName")) {
         this.eventName = json.get("eventName").getAsString();
      }

      if (json.has("eventTime")) {
         this.eventTime = json.get("eventTime").getAsDouble();
      }

      super.loadFromJSON(json);
   }

   public void onTick(World world, MobEventPlayerServer mobEventPlayerServer) {
      if (mobEventPlayerServer != null) {
         if ("".equals(this.eventName) || this.eventName.equals(mobEventPlayerServer.mobEvent.name)) {
            int triggerTick = Math.round((float)mobEventPlayerServer.mobEvent.duration * (float)this.eventTime);
            if (mobEventPlayerServer.ticks == triggerTick) {
               this.trigger(world, mobEventPlayerServer.player, mobEventPlayerServer.origin, mobEventPlayerServer.level, 0);
            }
         }
      }
   }
}
