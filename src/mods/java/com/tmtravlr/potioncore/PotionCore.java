package com.tmtravlr.potioncore;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.tmtravlr.potioncore.network.CToSMessage;
import com.tmtravlr.potioncore.network.PacketHandlerClient;
import com.tmtravlr.potioncore.network.PacketHandlerServer;
import com.tmtravlr.potioncore.network.SToCMessage;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "potioncore",
   version = "1.9_for_1.12.2",
   name = "Potion Core",
   dependencies = "required-after:forge@[14.23.0.2524,);",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class PotionCore {
   public static final String MOD_ID = "potioncore";
   public static final String VERSION = "1.9_for_1.12.2";
   public static final String MOD_NAME = "Potion Core";
   public static PotionCore instance;
   public static CommonProxy proxy;
   public static SimpleNetworkWrapper networkWrapper;
   public static Logger logger;

   public PotionCore() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.tmtravlr.potioncore.ClientProxy() : new com.tmtravlr.potioncore.CommonProxy();
         com.tmtravlr.potioncore.PotionCoreEventHandler.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.tmtravlr.potioncore.PotionCoreEventHandlerClient.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      ConfigLoader.config = new Configuration(event.getSuggestedConfigurationFile());
      ConfigLoader.load();
      proxy.loadInverted();
      networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("potioncore");
      networkWrapper.registerMessage(new PacketHandlerServer(), CToSMessage.class, CToSMessage::new, 0, Side.SERVER);
      networkWrapper.registerMessage(new PacketHandlerClient(), SToCMessage.class, SToCMessage::new, 1, Side.CLIENT);
   }

   public void init(FMLInitializationEvent event) {
      if (ConfigLoader.fixReach) {
         proxy.replaceEntityRenderer();
      }

   }

   public void postInit(FMLPostInitializationEvent event) {
      PotionCoreEffects.loadGoodBadEffects();
      PotionCoreEffects.loadInversions();
   }
}
