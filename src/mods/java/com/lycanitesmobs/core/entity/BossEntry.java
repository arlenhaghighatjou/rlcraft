package com.lycanitesmobs.core.entity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;

public class BossEntry {
   public EaglercraftUUID uuid;
   public Entity entity;
   public long lastUpdated = -1L;
   public int nearbyRange = 30;

   public BossEntry() {
   }

   public void update(Entity entity) {
      this.uuid = entity.getUniqueID();
      this.entity = entity;
      this.lastUpdated = entity.getEntityWorld().getTotalWorldTime();
      if (entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity creatureEntity = (BaseCreatureEntity)entity;
         if (creatureEntity.spawnedWithBlockProtection > 0) {
            this.nearbyRange = creatureEntity.spawnedWithBlockProtection;
         } else if (creatureEntity.creatureInfo != null) {
            this.nearbyRange = creatureEntity.creatureInfo.bossNearbyRange;
         }
      }

   }
}
