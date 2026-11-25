package com.charles445.aireducer.handler;

import com.charles445.aireducer.AIReducer;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.process.AnyProcess;
import com.charles445.aireducer.process.IAFOldProcess;
import com.charles445.aireducer.process.IAFProcess;
import com.charles445.aireducer.process.ModProcess;
import com.charles445.aireducer.process.VanillaProcess;
import com.charles445.aireducer.reflect.ReflectorIAF;
import com.charles445.aireducer.reflect.ReflectorIAFOld;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpawnHandler {
   public Map<String, ModProcess> modProcessMap;
   public ModProcess anyProcess;

   public SpawnHandler() {
      try {
         new ReflectorMinecraft();
      } catch (Exception var5) {
         AIReducer.logger.error("Failed to setup ReflectorMinecraft!", var5);
      }

      try {
         if (Loader.isModLoaded("iceandfire")) {
            Class c_iaf = Class.forName("com.github.alexthe666.iceandfire.IceAndFire");
            Field f_iaf_VERSION = ReflectUtil.findField(c_iaf, "VERSION");
            String iaf_version = (String)f_iaf_VERSION.get((Object)null);
            if (iaf_version.equals("1.7.1")) {
               new ReflectorIAFOld();
            } else {
               new ReflectorIAF();
            }
         }
      } catch (Exception var4) {
         AIReducer.logger.error("Failed to setup ReflectorIAF!", var4);
      }

      this.modProcessMap = new ConcurrentHashMap();
      this.anyProcess = new AnyProcess();
      this.modProcessMap.put("minecraft", new VanillaProcess());
      if (Loader.isModLoaded("iceandfire")) {
         if (ReflectorIAF.reflector != null) {
            this.modProcessMap.put("iceandfire", new IAFProcess());
         }

         if (ReflectorIAFOld.reflector != null) {
            this.modProcessMap.put("iceandfire", new IAFOldProcess());
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (ModConfig.enabled) {
         if (!event.getWorld().isRemote) {
            Entity rawEntity = event.getEntity();
            if (rawEntity instanceof EntityLiving) {
               EntityLiving entity = (EntityLiving)rawEntity;
               ResourceLocation id = EntityList.getKey(entity.getClass());
               if (id == null) {
                  return;
               }

               String domain = id.getResourceDomain();
               String path = id.getResourcePath();
               this.anyProcess.handle(entity, domain, path);
               ModProcess modProcess = (ModProcess)this.modProcessMap.get(domain);
               if (modProcess != null && modProcess.canUse()) {
                  modProcess.handle(entity, domain, path);
               }
            }

         }
      }
   }
}
