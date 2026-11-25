package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventSpawnCondition extends SpawnCondition {
   public String eventName = "";
   public int eventTimeMin = -1;
   public int eventTimeMax = -1;

   public EventSpawnCondition() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("eventName")) {
         this.eventName = json.get("eventName").getAsString();
      }

      if (json.has("eventTimeMin")) {
         this.eventTimeMin = json.get("eventTimeMin").getAsInt();
      }

      if (json.has("eventTimeMax")) {
         this.eventTimeMax = json.get("eventTimeMax").getAsInt();
      }

      super.loadFromJSON(json);
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
      if (worldExt == null) {
         return false;
      } else if ("".equals(this.eventName) || worldExt.getWorldEventName() != null && worldExt.getWorldEventName().equalsIgnoreCase(this.eventName)) {
         if (this.eventTimeMin >= 0 && worldExt.getWorldEventCount() < this.eventTimeMin) {
            return false;
         } else {
            return this.eventTimeMax >= 0 && worldExt.getWorldEventCount() > this.eventTimeMax ? false : super.isMet(world, player, position);
         }
      } else {
         return false;
      }
   }
}
