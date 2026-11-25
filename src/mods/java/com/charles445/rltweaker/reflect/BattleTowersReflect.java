package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.Loader;

public class BattleTowersReflect {
   public final Class c_AS_BattleTowersCore = Class.forName("atomicstryker.battletowers.common.AS_BattleTowersCore");
   public final Method m_AS_BattleTowersCore_getTowerDestroyers;
   public final Field f_AS_BattleTowersCore_instance;
   public final Object o_AS_BattleTowersCore_instance;
   public final Field f_AS_BattleTowersCore_minDistanceFromSpawn;
   public final Field f_AS_BattleTowersCore_minDistanceBetweenTowers;
   public final Field f_AS_BattleTowersCore_towerDestroyerEnabled;
   public final Class c_AS_EntityGolem;
   public final Method m_AS_EntityGolem_getIsDormant;
   public final Class c_AS_TowerDestroyer;
   public final Field f_AS_TowerDestroyer_player;
   public final Field f_AS_TowerDestroyer_deleteMe;
   public final Class c_AS_EntityGolemFireball;
   public final Field f_AS_EntityGolemFireball_shooterEntity;
   public final Field f_AS_EntityGolemFireball_accelerationX;
   public final Field f_AS_EntityGolemFireball_accelerationY;
   public final Field f_AS_EntityGolemFireball_accelerationZ;
   public final Class c_WorldGenHandler;
   public final Field f_WorldGenHandler_instance;
   public final Method m_WorldGenHandler_attemptToSpawnTower;
   public final Method m_WorldGenHandler_getWorldHandle;
   public final Method m_WorldGenHandler_getIsChunkProviderAllowed;
   public final Method m_WorldGenHandler_getIsBiomeAllowed;
   public final Method m_WorldGenHandler_getSurfaceBlockHeight;
   public final Class c_WorldGenHandler$TowerPosition;
   public final Constructor con_WorldGenHandler$TowerPosition;
   public final Class c_WorldGenHandler$WorldHandle;
   public final Field f_WorldGenHandler$WorldHandle_disableGenerationHook;
   private boolean isLycanitesAvailable = false;
   @Nullable
   private Class c_LM_ProjectileInfo;
   @Nullable
   private Method m_LM_ProjectileInfo_createProjectile;
   @Nullable
   private Class c_LM_ProjectileManager;
   @Nullable
   private Method m_LM_ProjectileManager_getInstance;
   @Nullable
   private Method m_LM_ProjectileManager_getProjectile;
   @Nullable
   private Class c_LM_BaseProjectileEntity;
   @Nullable
   private Method m_LM_BaseProjectileEntity_getProjectileScale;
   @Nullable
   private Method m_LM_BaseProjectileEntity_setProjectileScale;

   public BattleTowersReflect() throws Exception {
      this.m_AS_BattleTowersCore_getTowerDestroyers = ReflectUtil.findMethod(this.c_AS_BattleTowersCore, "getTowerDestroyers");
      this.f_AS_BattleTowersCore_instance = ReflectUtil.findField(this.c_AS_BattleTowersCore, "instance");
      this.o_AS_BattleTowersCore_instance = this.f_AS_BattleTowersCore_instance.get((Object)null);
      this.f_AS_BattleTowersCore_minDistanceFromSpawn = ReflectUtil.findField(this.c_AS_BattleTowersCore, "minDistanceFromSpawn");
      this.f_AS_BattleTowersCore_minDistanceBetweenTowers = ReflectUtil.findField(this.c_AS_BattleTowersCore, "minDistanceBetweenTowers");
      this.f_AS_BattleTowersCore_towerDestroyerEnabled = ReflectUtil.findField(this.c_AS_BattleTowersCore, "towerDestroyerEnabled");
      this.c_AS_EntityGolem = Class.forName("atomicstryker.battletowers.common.AS_EntityGolem");
      this.m_AS_EntityGolem_getIsDormant = ReflectUtil.findMethod(this.c_AS_EntityGolem, "getIsDormant");
      this.c_AS_TowerDestroyer = Class.forName("atomicstryker.battletowers.common.AS_TowerDestroyer");
      this.f_AS_TowerDestroyer_player = ReflectUtil.findField(this.c_AS_TowerDestroyer, "player");
      this.f_AS_TowerDestroyer_deleteMe = ReflectUtil.findField(this.c_AS_TowerDestroyer, "deleteMe");
      this.c_AS_EntityGolemFireball = Class.forName("atomicstryker.battletowers.common.AS_EntityGolemFireball");
      this.f_AS_EntityGolemFireball_shooterEntity = ReflectUtil.findField(this.c_AS_EntityGolemFireball, "shooterEntity");
      this.f_AS_EntityGolemFireball_accelerationX = ReflectUtil.findField(this.c_AS_EntityGolemFireball, "accelerationX");
      this.f_AS_EntityGolemFireball_accelerationY = ReflectUtil.findField(this.c_AS_EntityGolemFireball, "accelerationY");
      this.f_AS_EntityGolemFireball_accelerationZ = ReflectUtil.findField(this.c_AS_EntityGolemFireball, "accelerationZ");
      this.c_WorldGenHandler = Class.forName("atomicstryker.battletowers.common.WorldGenHandler");
      this.f_WorldGenHandler_instance = ReflectUtil.findField(this.c_WorldGenHandler, "instance");
      this.m_WorldGenHandler_attemptToSpawnTower = ReflectUtil.findMethod(this.c_WorldGenHandler, "attemptToSpawnTower");
      this.m_WorldGenHandler_getWorldHandle = ReflectUtil.findMethod(this.c_WorldGenHandler, "getWorldHandle");
      this.m_WorldGenHandler_getIsChunkProviderAllowed = ReflectUtil.findMethod(this.c_WorldGenHandler, "getIsChunkProviderAllowed");
      this.m_WorldGenHandler_getIsBiomeAllowed = ReflectUtil.findMethod(this.c_WorldGenHandler, "getIsBiomeAllowed");
      this.m_WorldGenHandler_getSurfaceBlockHeight = ReflectUtil.findMethod(this.c_WorldGenHandler, "getSurfaceBlockHeight");
      this.c_WorldGenHandler$TowerPosition = Class.forName("atomicstryker.battletowers.common.WorldGenHandler$TowerPosition");
      this.con_WorldGenHandler$TowerPosition = this.c_WorldGenHandler$TowerPosition.getDeclaredConstructor(this.c_WorldGenHandler, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE);
      this.c_WorldGenHandler$WorldHandle = Class.forName("atomicstryker.battletowers.common.WorldGenHandler$WorldHandle");
      this.f_WorldGenHandler$WorldHandle_disableGenerationHook = ReflectUtil.findField(this.c_WorldGenHandler$WorldHandle, "disableGenerationHook");
      if (Loader.isModLoaded("lycanitesmobs")) {
         try {
            this.c_LM_ProjectileInfo = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileInfo");
            this.c_LM_ProjectileManager = Class.forName("com.lycanitesmobs.core.info.projectile.ProjectileManager");
            this.c_LM_BaseProjectileEntity = Class.forName("com.lycanitesmobs.core.entity.BaseProjectileEntity");
            this.m_LM_ProjectileInfo_createProjectile = ReflectUtil.findMethod(this.c_LM_ProjectileInfo, "createProjectile", World.class, EntityLivingBase.class);
            this.m_LM_ProjectileManager_getInstance = ReflectUtil.findMethod(this.c_LM_ProjectileManager, "getInstance");
            this.m_LM_ProjectileManager_getProjectile = ReflectUtil.findMethod(this.c_LM_ProjectileManager, "getProjectile");
            this.m_LM_BaseProjectileEntity_getProjectileScale = ReflectUtil.findMethod(this.c_LM_BaseProjectileEntity, "getProjectileScale");
            this.m_LM_BaseProjectileEntity_setProjectileScale = ReflectUtil.findMethod(this.c_LM_BaseProjectileEntity, "setProjectileScale");
            this.isLycanitesAvailable = true;
         } catch (Exception var2) {
            this.isLycanitesAvailable = false;
         }
      }

   }

   public boolean isEntityGolem(Entity entity) {
      return this.c_AS_EntityGolem.isInstance(entity);
   }

   public boolean getIsDormant(Object golem) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_AS_EntityGolem_getIsDormant.invoke(golem)).booleanValue();
   }

   public Set<Object> getTowerDestroyers() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (Set)this.m_AS_BattleTowersCore_getTowerDestroyers.invoke((Object)null);
   }

   public void setDestroyerPlayer(Object towerDestroyer, @Nullable Entity entityToSet) throws IllegalArgumentException, IllegalAccessException {
      this.f_AS_TowerDestroyer_player.set(towerDestroyer, entityToSet);
   }

   public boolean getDestroyerDeleteMe(Object towerDestroyer) throws IllegalArgumentException, IllegalAccessException {
      return this.f_AS_TowerDestroyer_deleteMe.getBoolean(towerDestroyer);
   }

   public void setDestroyerDeleteMe(Object towerDestroyer, boolean deleteMe) throws IllegalArgumentException, IllegalAccessException {
      this.f_AS_TowerDestroyer_deleteMe.setBoolean(towerDestroyer, deleteMe);
   }

   public boolean isEntityGolemFireball(Entity entity) {
      return this.c_AS_EntityGolemFireball.isInstance(entity);
   }

   public double getGolemFireballAccelerationX(Object golemFireball) throws IllegalArgumentException, IllegalAccessException {
      return this.f_AS_EntityGolemFireball_accelerationX.getDouble(golemFireball);
   }

   public double getGolemFireballAccelerationY(Object golemFireball) throws IllegalArgumentException, IllegalAccessException {
      return this.f_AS_EntityGolemFireball_accelerationY.getDouble(golemFireball);
   }

   public double getGolemFireballAccelerationZ(Object golemFireball) throws IllegalArgumentException, IllegalAccessException {
      return this.f_AS_EntityGolemFireball_accelerationZ.getDouble(golemFireball);
   }

   private Object getWorldGenHandler() throws IllegalArgumentException, IllegalAccessException {
      return this.f_WorldGenHandler_instance.get((Object)null);
   }

   @Nullable
   public Object getWorldHandle(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      Object wghInstance = this.getWorldGenHandler();
      return wghInstance == null ? null : this.m_WorldGenHandler_getWorldHandle.invoke(wghInstance, world);
   }

   public boolean setWorldDisableGenerationHook(World world, int value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      Object wh = this.getWorldHandle(world);
      if (wh == null) {
         return false;
      } else {
         this.f_WorldGenHandler$WorldHandle_disableGenerationHook.setInt(wh, value);
         return true;
      }
   }

   public boolean getIsChunkProviderAllowed(Object worldGenHandler, IChunkProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_WorldGenHandler_getIsChunkProviderAllowed.invoke(worldGenHandler, provider)).booleanValue();
   }

   public boolean getIsBiomeAllowed(Object worldGenHandler, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_WorldGenHandler_getIsBiomeAllowed.invoke(worldGenHandler, biome)).booleanValue();
   }

   public int getSurfaceBlockHeight(Object worldGenHandler, World world, int x, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Integer)this.m_WorldGenHandler_getSurfaceBlockHeight.invoke(worldGenHandler, world, x, z)).intValue();
   }

   public Object getModInstance() {
      return this.o_AS_BattleTowersCore_instance;
   }

   public int getMinDistanceFromSpawn() throws IllegalArgumentException, IllegalAccessException {
      return ((Integer)this.f_AS_BattleTowersCore_minDistanceFromSpawn.get(this.getModInstance())).intValue();
   }

   public int getMinDistanceBetweenTowers() throws IllegalArgumentException, IllegalAccessException {
      return ((Integer)this.f_AS_BattleTowersCore_minDistanceBetweenTowers.get(this.getModInstance())).intValue();
   }

   public int getTowerDestroyerEnabled() throws IllegalArgumentException, IllegalAccessException {
      return this.f_AS_BattleTowersCore_towerDestroyerEnabled.getInt(this.getModInstance());
   }

   public void setTowerDestroyerEnabled(int val) throws IllegalArgumentException, IllegalAccessException {
      this.f_AS_BattleTowersCore_towerDestroyerEnabled.setInt(this.getModInstance(), val);
   }

   public Object newTowerPosition(Object worldGenHandler, int x, int y, int z, int type, boolean underground) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.con_WorldGenHandler$TowerPosition.newInstance(worldGenHandler, x, y, z, type, underground);
   }

   public boolean attemptToSpawnTower(Object worldGenHandler, World world, Object towerPosition, Random random, int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_WorldGenHandler_attemptToSpawnTower.invoke(worldGenHandler, world, towerPosition, random, x, y, z)).booleanValue();
   }

   public boolean isLycanitesAvailable() {
      return this.isLycanitesAvailable;
   }

   @Nullable
   public Entity createLycanitesProjectileWithNameAndShooter(String projectileName, Entity shooter, float scaleModifier) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (!this.isLycanitesAvailable()) {
         return null;
      } else if (this.c_LM_ProjectileInfo != null && this.m_LM_ProjectileInfo_createProjectile != null && this.c_LM_ProjectileManager != null && this.m_LM_ProjectileManager_getInstance != null && this.m_LM_ProjectileManager_getProjectile != null && this.c_LM_BaseProjectileEntity != null && this.m_LM_BaseProjectileEntity_getProjectileScale != null && this.m_LM_BaseProjectileEntity_setProjectileScale != null) {
         if (shooter == null) {
            return null;
         } else {
            Object projManagerInst = this.m_LM_ProjectileManager_getInstance.invoke((Object)null);
            if (projManagerInst == null) {
               return null;
            } else {
               Object projectileInfo = this.m_LM_ProjectileManager_getProjectile.invoke(projManagerInst, projectileName);
               if (projectileInfo == null) {
                  return null;
               } else {
                  Object createdProjectile = this.m_LM_ProjectileInfo_createProjectile.invoke(projectileInfo, shooter.getEntityWorld(), shooter);
                  if (createdProjectile == null) {
                     return null;
                  } else {
                     Float currentScale = (Float)this.m_LM_BaseProjectileEntity_getProjectileScale.invoke(createdProjectile);
                     if (currentScale == null) {
                        return null;
                     } else {
                        this.m_LM_BaseProjectileEntity_setProjectileScale.invoke(createdProjectile, currentScale.floatValue() * scaleModifier);
                        return (Entity)createdProjectile;
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }
}
