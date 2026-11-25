package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class VillageSpawnLocation extends RandomSpawnLocation {
   public int villageRange = 128;

   public VillageSpawnLocation() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      if (json.has("villageRange")) {
         this.villageRange = json.get("villageRange").getAsInt();
      }

   }

   public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
      LycanitesMobs.logDebug("JSONSpawner", "Getting Nearest Village Within Range");
      Village village = world.getVillageCollection().getNearestVillage(triggerPos, this.villageRange);
      if (village == null) {
         LycanitesMobs.logDebug("JSONSpawner", "No Village within range found.");
         return new ArrayList();
      } else {
         double villageDistance = Math.sqrt(village.getCenter().distanceSq(triggerPos));
         if (villageDistance > (double)this.villageRange) {
            LycanitesMobs.logDebug("JSONSpawner", "No Village within range, nearest was: " + villageDistance + " at: " + village.getCenter());
            return new ArrayList();
         } else {
            LycanitesMobs.logDebug("JSONSpawner", "Found a Village within range, at: " + village.getCenter());
            return super.getSpawnPositions(world, player, village.getCenter());
         }
      }
   }
}
