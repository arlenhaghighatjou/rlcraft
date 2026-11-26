package com.tmtravlr.qualitytools;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.tmtravlr.qualitytools.config.CommandQualityToolsReload;
import com.tmtravlr.qualitytools.config.ConfigLoader;
import com.tmtravlr.qualitytools.network.CToSMessage;
import com.tmtravlr.qualitytools.network.PacketHandlerClient;
import com.tmtravlr.qualitytools.network.PacketHandlerServer;
import com.tmtravlr.qualitytools.network.SToCMessage;
import com.tmtravlr.qualitytools.reforging.BlockReforgingStation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "qualitytools",
   name = "Quality Tools",
   version = "1.0.7_for_1.12.2",
   dependencies = "required-after:potioncore;after:baubles",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class QualityToolsMod {
   public static final String MOD_NAME = "Quality Tools";
   public static final String MOD_ID = "qualitytools";
   public static final String VERSION = "1.0.7_for_1.12.2";
   public static QualityToolsMod instance;
   public static CommonProxy proxy;
   public static SimpleNetworkWrapper networkWrapper;
   public static Logger logger;
   public static CreativeTabs tabQualityTools = new CreativeTabs("qualitytools") {
      public ItemStack getTabIconItem() {
         return new ItemStack(BlockReforgingStation.INSTANCE);
      }
   };

   public QualityToolsMod() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.tmtravlr.qualitytools.ClientProxy() : new com.tmtravlr.qualitytools.CommonProxy();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.tmtravlr.qualitytools.ClientEventHandler.register(MinecraftForge.EVENT_BUS);
         }
         com.tmtravlr.qualitytools.CommonEventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onServerStarting((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      ConfigLoader.loadPreInit(event);
      logger = event.getModLog();
      networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("qualitytools");
      networkWrapper.registerMessage(new PacketHandlerServer(), CToSMessage.class, CToSMessage::new, 0, Side.SERVER);
      networkWrapper.registerMessage(new PacketHandlerClient(), SToCMessage.class, SToCMessage::new, 1, Side.CLIENT);
   }

   public void init(FMLInitializationEvent event) {
      proxy.registerRenderers();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new QualityToolsGuiHandler());
   }

   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandQualityToolsReload());
      ConfigLoader.reloadConfigs();
   }
}
