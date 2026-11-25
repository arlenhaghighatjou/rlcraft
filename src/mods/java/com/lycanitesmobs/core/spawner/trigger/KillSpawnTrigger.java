package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class KillSpawnTrigger extends EntitySpawnTrigger {
   public KillSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public void onKill(EntityPlayer player, EntityLiving killedEntity) {
      if (this.isMatchingEntity(killedEntity)) {
         if (this.chance >= 1.0D || player.getRNG().nextDouble() <= this.chance) {
            this.trigger(player.getEntityWorld(), player, killedEntity.getPosition(), 0, 0);
         }
      }
   }
}
