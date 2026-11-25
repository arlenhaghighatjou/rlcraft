package net.konwboy.tumbleweed;

import net.konwboy.tumbleweed.client.RenderTumbleweed;
import net.konwboy.tumbleweed.common.EntityTumbleweed;
import net.konwboy.tumbleweed.common.TumbleweedConfig;
import net.konwboy.tumbleweed.common.TumbleweedSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "tumbleweed",
   name = "Tumbleweed",
   version = "1.12-0.4.7",
   useMetadata = true,
   acceptedMinecraftVersions = "[1.11,1.12.2]"
)
public class Tumbleweed {
   public static final String MOD_ID = "tumbleweed";
   public static final String MOD_NAME = "Tumbleweed";
   public static Logger logger = LogManager.getLogger("tumbleweed");

   public Tumbleweed() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         TumbleweedConfig.register();
      } else if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         postInit((FMLPostInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "tumbleweed"), EntityTumbleweed.class,
         "Tumbleweed", 0, this, 128, 30, true, EntityTumbleweed::new);
      if (event.getSide() == Side.CLIENT) {
         RenderingRegistry.registerEntityRenderingHandler(EntityTumbleweed.class, new RenderTumbleweed.Factory());
      }

   }

   public void init(FMLInitializationEvent event) {
      TumbleweedSpawner spawner = new TumbleweedSpawner();
      MinecraftForge.EVENT_BUS.register(WorldTickEvent.class, spawner::onTick);
      MinecraftForge.EVENT_BUS.register(CanUpdate.class, spawner::onCanUpdate);
      MinecraftForge.EVENT_BUS.register(OnConfigChangedEvent.class, this::onConfigChangedEvent);
   }

   public void postInit(FMLPostInitializationEvent event) {
      TumbleweedConfig.Logic.reload();
   }

   public void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("tumbleweed")) {
         ConfigManager.sync("tumbleweed", Type.INSTANCE);
         TumbleweedConfig.Logic.reload();
      }

   }
}
