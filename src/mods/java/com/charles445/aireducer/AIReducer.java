package com.charles445.aireducer;

import com.charles445.aireducer.config.JsonConfig;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.handler.SpawnHandler;
import java.io.File;
import java.util.Map.Entry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "aireducer",
   name = "AI Reducer",
   version = "0.3.0",
   acceptedMinecraftVersions = "[1.12]",
   acceptableRemoteVersions = "*"
)
public class AIReducer {
   public static final String MODID = "aireducer";
   public static final String NAME = "AI Reducer";
   public static final String VERSION = "0.3.0";
   @Instance("aireducer")
   public static AIReducer instance;
   public static Logger logger = LogManager.getLogger("AIReducer");
   public static File jsonDirectory;

   public AIReducer() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      jsonDirectory = new File(event.getModConfigurationDirectory(), "aireducer");
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      JsonConfig.init();
      if (ModConfig.debug) {
         this.printRegisteredEntities(false);
      }

      MinecraftForge.EVENT_BUS.register(new SpawnHandler());
   }

   private void printRegisteredEntities(boolean dumpAll) {
      for(Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries()) {
         if (!dumpAll) {
            if (EntityLiving.class.isAssignableFrom(((EntityEntry)entry.getValue()).getEntityClass())) {
               logger.debug(((ResourceLocation)entry.getKey()).toString());
            } else if (((EntityEntry)entry.getValue()).getEntityClass().isAssignableFrom(EntityLiving.class)) {
               logger.debug(((ResourceLocation)entry.getKey()).toString());
            }
         } else {
            logger.debug(((ResourceLocation)entry.getKey()).toString());
         }
      }

   }
}
