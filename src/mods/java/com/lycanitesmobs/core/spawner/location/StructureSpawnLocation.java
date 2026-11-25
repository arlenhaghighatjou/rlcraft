package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class StructureSpawnLocation extends RandomSpawnLocation {
   public String structureType = "Stronghold";
   public int structureRange = 128;

   public StructureSpawnLocation() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      if (json.has("structureType")) {
         this.structureType = json.get("structureType").getAsString();
      }

      if (json.has("structureRange")) {
         this.structureRange = json.get("structureRange").getAsInt();
      }

   }

   public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
      LycanitesMobs.logDebug("JSONSpawner", "Getting Nearest Structures Within Range");
      if (!(world instanceof WorldServer)) {
         LycanitesMobs.logWarning("", "[JSONSpawner] Structure spawn location was called with a non ServerWorld World instance.");
         return new ArrayList();
      } else {
         BlockPos structurePos = null;

         try {
            structurePos = ((WorldServer)world).getChunkProvider().getNearestStructurePos(world, this.structureType, triggerPos, false);
         } catch (Exception var7) {
            ;
         }

         if (structurePos == null) {
            LycanitesMobs.logDebug("JSONSpawner", "No Structures found.");
            return new ArrayList();
         } else {
            double structureDistance = Math.sqrt(structurePos.distanceSq(triggerPos));
            if (structureDistance > (double)this.structureRange) {
               LycanitesMobs.logDebug("JSONSpawner", "No Structures within range, nearest was: " + structureDistance + " at: " + structurePos);
               return new ArrayList();
            } else {
               LycanitesMobs.logDebug("JSONSpawner", "Found a Structures within range, at: " + structurePos);
               return super.getSpawnPositions(world, player, structurePos);
            }
         }
      }
   }
}
