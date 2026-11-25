package com.lycanitesmobs.core.mobevent;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.world.World;

public class MobEventSchedule {
   public MobEvent mobEvent;
   public int dimensionId = 0;
   public int worldDay = 0;
   public int dayTime = 100;

   public static MobEventSchedule createFromJSON(JsonObject json) {
      MobEvent mobEvent = null;
      if (json.has("eventName")) {
         mobEvent = MobEventManager.getInstance().getMobEvent(json.get("eventName").getAsString());
      }

      MobEventSchedule mobEventSchedule = new MobEventSchedule(mobEvent);
      mobEventSchedule.loadFromJSON(json);
      return mobEventSchedule;
   }

   public MobEventSchedule(MobEvent mobEvent) {
      this.mobEvent = mobEvent;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("dimensionId")) {
         this.dimensionId = json.get("dimensionId").getAsInt();
      }

      if (json.has("worldDay")) {
         this.worldDay = json.get("worldDay").getAsInt();
      }

      if (json.has("dayTime")) {
         this.dayTime = json.get("dayTime").getAsInt();
      }

   }

   public boolean canStart(World world) {
      if (world == null) {
         return false;
      } else if (world.provider.getDimension() != this.dimensionId) {
         return false;
      } else {
         int time = (int)Math.floor((double)world.getWorldTime() % 24000.0D);
         int day = (int)(Math.floor((double)world.getTotalWorldTime()) / 23999.0D);
         if (day != this.worldDay) {
            return false;
         } else {
            return time == this.dayTime;
         }
      }
   }

   public void start(ExtendedWorld worldExt) {
      if (this.mobEvent == null) {
         MobEventListener.getInstance().triggerRandomMobEvent(worldExt.world, worldExt);
      } else {
         worldExt.startWorldEvent(this.mobEvent);
      }
   }
}
