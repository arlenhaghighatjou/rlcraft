package com.lycanitesmobs.core.info.projectile;

import com.lycanitesmobs.core.LycanitesClasses;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.entity.EntityProjectileModel;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import com.lycanitesmobs.core.entity.PortalEntity;
import com.lycanitesmobs.core.entity.projectile.EntityDevilGatling;
import com.lycanitesmobs.core.entity.projectile.EntityHellLaser;
import com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd;
import com.lycanitesmobs.core.entity.projectile.EntityHellShield;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWall;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart;
import com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class ProjectileManager extends JSONLoader {
   public static ProjectileManager INSTANCE;
   public Map<String, ProjectileInfo> projectiles = new HashMap();
   public Map<String, Class<? extends Entity>> oldSpriteProjectiles = new HashMap();
   public Map<String, Class<? extends Entity>> oldModelProjectiles = new HashMap();
   protected int nextProjectileNetworkId = 1000;

   public ProjectileManager() {
   }

   public static ProjectileManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ProjectileManager();
      }

      return INSTANCE;
   }

   public void startup(ModInfo modInfo) {
      this.loadAllFromJSON(modInfo);

      for(ProjectileInfo projectileInfo : this.projectiles.values()) {
         projectileInfo.load();
      }

      this.loadOldProjectiles();
   }

   public void loadAllFromJSON(ModInfo groupInfo) {
      try {
         this.loadAllJson(groupInfo, "Projectile", "projectiles", "name", true);
         LycanitesMobs.logDebug("Projectile", "Complete! " + this.projectiles.size() + " JSON Projectile Info Loaded In Total.");
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "No Projectiles loaded for: " + groupInfo.name);
      }

   }

   public void parseJson(ModInfo modInfo, String name, JsonObject json) {
      ProjectileInfo projectileInfo = new ProjectileInfo(modInfo);
      projectileInfo.loadFromJSON(json);
      if (projectileInfo.name == null) {
         LycanitesMobs.logWarning("", "[Projectile] Unable to load " + name + " json due to missing name.");
      } else {
         if (this.projectiles.containsKey(projectileInfo.name)) {
            projectileInfo = (ProjectileInfo)this.projectiles.get(projectileInfo.name);
            projectileInfo.loadFromJSON(json);
         }

         this.projectiles.put(projectileInfo.name, projectileInfo);
      }
   }

   public int getNextProjectileNetworkId() {
      return this.nextProjectileNetworkId++;
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, EntityEntry.class, this::registerEntities);
   }

   public void registerEntities(Register<EntityEntry> event) {
      ModInfo modInfo = LycanitesMobs.modInfo;
      LycanitesMobs.logDebug("Projectile", "Forge registering all " + this.projectiles.size() + " projectiles from the mod: " + modInfo.name + "...");

      for(ProjectileInfo projectileInfo : this.projectiles.values()) {
         if (projectileInfo.modInfo == modInfo) {
            EntityEntry entityEntry = EntityEntryBuilder.create().entity(projectileInfo.entityClass).factory(LycanitesClasses.worldFactory(projectileInfo.entityClass)).id(projectileInfo.getEntityId(), this.getNextProjectileNetworkId()).name(projectileInfo.getEntityId()).tracker(40, 3, true).build();
            event.getRegistry().register(entityEntry);
         }
      }

      for(String entityName : this.oldSpriteProjectiles.keySet()) {
         String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;
         EntityEntry entityEntry = EntityEntryBuilder.create().entity((Class)this.oldSpriteProjectiles.get(entityName)).factory(LycanitesClasses.worldFactory(this.oldSpriteProjectiles.get(entityName))).id(registryName, this.getNextProjectileNetworkId()).name(registryName).tracker(40, 3, true).build();
         event.getRegistry().register(entityEntry);
      }

      for(String entityName : this.oldModelProjectiles.keySet()) {
         String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;
         EntityEntry entityEntry = EntityEntryBuilder.create().entity((Class)this.oldModelProjectiles.get(entityName)).factory(LycanitesClasses.worldFactory(this.oldModelProjectiles.get(entityName))).id(registryName, this.getNextProjectileNetworkId()).name(registryName).tracker(40, 3, true).build();
         event.getRegistry().register(entityEntry);
      }

   }

   public ProjectileInfo getProjectile(String projectileName) {
      return !this.projectiles.containsKey(projectileName) ? null : (ProjectileInfo)this.projectiles.get(projectileName);
   }

   public void loadOldProjectiles() {
      this.addOldProjectile("summoningportal", PortalEntity.class);
      this.addOldProjectile("laserend", LaserEndProjectileEntity.class);
      this.addOldProjectile("shadowfirebarrier", EntityShadowfireBarrier.class, false);
      this.addOldProjectile("hellfirewall", EntityHellfireWall.class, false);
      this.addOldProjectile("hellfireorb", EntityHellfireOrb.class, false);
      this.addOldProjectile("hellfirewave", EntityHellfireWave.class, false);
      this.addOldProjectile("hellfirewavepart", EntityHellfireWavePart.class, false);
      this.addOldProjectile("hellfirebarrier", EntityHellfireBarrier.class, false);
      this.addOldProjectile("hellfirebarrierpart", EntityHellfireBarrierPart.class, false);
      this.addOldProjectile("devilgatling", EntityDevilGatling.class, false);
      this.addOldProjectile("hellshield", EntityHellShield.class, false);
      this.addOldProjectile("helllaser", EntityHellLaser.class, false);
      this.addOldProjectile("helllaserend", EntityHellLaserEnd.class, false);
   }

   public void addOldProjectile(String name, Class<? extends Entity> entityClass) {
      if (EntityProjectileModel.class.isAssignableFrom(entityClass)) {
         this.oldModelProjectiles.put(name, entityClass);
      } else {
         this.oldSpriteProjectiles.put(name, entityClass);
      }
   }

   public void addOldProjectile(String name, Class<? extends Entity> entityClass, boolean impactSound) {
      ModInfo modInfo = LycanitesMobs.modInfo;
      AssetManager.addSound(name, modInfo, "projectile." + name);
      if (impactSound) {
         AssetManager.addSound(name + "_impact", modInfo, "projectile." + name + ".impact");
      }

      this.addOldProjectile(name, entityClass);
   }
}
