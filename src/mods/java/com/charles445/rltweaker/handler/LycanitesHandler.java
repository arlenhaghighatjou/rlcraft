package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LycanitesHandler {
   private static final String RLTWEAKER_LYCANITES_BOSS = "rltweaker.lycanitesboss";
   private static final String RLTWEAKER_LYCANITES_BOSS_RANGE = "rltweaker.lycanitesbossrange";
   boolean forgeDataReflectionFailed = false;
   @Nullable
   Class c_ExtendedWorld = null;
   @Nullable
   Method m_ExtendedWorld_getForWorld = null;
   @Nullable
   Method m_ExtendedWorld_bossUpdate = null;
   @Nullable
   Method m_ExtendedWorld_overrideBossRange = null;

   public LycanitesHandler() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public static void setLycanitesBoss(Entity entity, boolean isBoss) {
      if (entity != null) {
         NBTTagCompound data = entity.getEntityData();
         if (data != null) {
            data.setBoolean("rltweaker.lycanitesboss", isBoss);
         }
      }
   }

   public static void setLycanitesBossRange(Entity entity, int range) {
      if (entity != null) {
         NBTTagCompound data = entity.getEntityData();
         if (data != null) {
            data.setInteger("rltweaker.lycanitesbossrange", range);
         }
      }
   }

   public static boolean getLycanitesBoss(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         NBTTagCompound data = entity.getEntityData();
         return data == null ? false : data.getBoolean("rltweaker.lycanitesboss");
      }
   }

   public static int getLycanitesBossRange(Entity entity) {
      if (entity == null) {
         return 0;
      } else {
         NBTTagCompound data = entity.getEntityData();
         return data == null ? 0 : data.getInteger("rltweaker.lycanitesbossrange");
      }
   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote) {
         if (!this.forgeDataReflectionFailed) {
            if (ModConfig.server.lycanitesmobs.enableEntityDataLycanitesBoss) {
               if (!getLycanitesBoss(event.getEntity())) {
                  return;
               }

               if (this.c_ExtendedWorld == null || this.m_ExtendedWorld_getForWorld == null || this.m_ExtendedWorld_bossUpdate == null || this.m_ExtendedWorld_overrideBossRange == null) {
                  try {
                     this.c_ExtendedWorld = Class.forName("com.lycanitesmobs.ExtendedWorld");
                     this.m_ExtendedWorld_getForWorld = ReflectUtil.findMethod(this.c_ExtendedWorld, "getForWorld");
                     this.m_ExtendedWorld_bossUpdate = ReflectUtil.findMethod(this.c_ExtendedWorld, "bossUpdate");
                     this.m_ExtendedWorld_overrideBossRange = ReflectUtil.findMethod(this.c_ExtendedWorld, "overrideBossRange");
                  } catch (Exception var5) {
                     this.forgeDataReflectionFailed = true;
                     RLTweaker.logger.error("Failed to reflect for Lycanites Boss Entity Data handling");
                     ErrorUtil.logSilent("LycanitesMobs Boss EntityData Reflection");
                     return;
                  }
               }

               try {
                  Object o_exWorld = this.m_ExtendedWorld_getForWorld.invoke((Object)null, event.getWorld());
                  if (o_exWorld != null) {
                     this.m_ExtendedWorld_bossUpdate.invoke(o_exWorld, event.getEntity());
                     int bossRange = getLycanitesBossRange(event.getEntity());
                     if (bossRange != 0) {
                        this.m_ExtendedWorld_overrideBossRange.invoke(o_exWorld, event.getEntity(), bossRange);
                     }
                  }
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
                  ErrorUtil.logSilent("LycanitesMobs Boss Update Invocation");
               }
            }

         }
      }
   }
}
