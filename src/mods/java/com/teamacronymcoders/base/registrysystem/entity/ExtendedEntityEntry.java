package com.teamacronymcoders.base.registrysystem.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class ExtendedEntityEntry extends EntityEntry {
   private SpawnInfo spawnInfo;
   private UpdateInfo updateInfo = new UpdateInfo();

   public ExtendedEntityEntry(Class<? extends Entity> entityClass, String name) {
      super(entityClass, name);
   }

   public SpawnInfo getSpawnInfo() {
      return this.spawnInfo;
   }

   public void setSpawnInfo(SpawnInfo spawnInfo) {
      this.spawnInfo = spawnInfo;
   }

   public UpdateInfo getUpdateInfo() {
      return this.updateInfo;
   }

   public void setUpdateInfo(UpdateInfo updateInfo) {
      this.updateInfo = updateInfo;
   }
}
