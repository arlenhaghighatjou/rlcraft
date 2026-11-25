package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntitySpawnedSpawnTrigger extends EntitySpawnTrigger {
   public EntitySpawnedSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public void onEntitySpawned(EntityLiving spawnedEntity) {
      if (this.isMatchingEntity(spawnedEntity)) {
         if (this.chance >= 1.0D || spawnedEntity.getRNG().nextDouble() <= this.chance) {
            this.trigger(spawnedEntity.getEntityWorld(), (EntityPlayer)null, spawnedEntity.getPosition(), 0, 0);
         }
      }
   }
}
