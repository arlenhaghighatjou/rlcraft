package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.network.CToSMessage;
import com.tmtravlr.potioncore.network.PacketHandlerClient;
import com.tmtravlr.potioncore.network.PacketHandlerServer;
import com.tmtravlr.potioncore.network.SToCMessage;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

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
   @Instance("potioncore")
   public static PotionCore instance;
   @SidedProxy(
      clientSide = "com.tmtravlr.potioncore.ClientProxy",
      serverSide = "com.tmtravlr.potioncore.CommonProxy"
   )
   public static CommonProxy proxy;
   public static SimpleNetworkWrapper networkWrapper;
   public static Logger logger;

   public PotionCore() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      ConfigLoader.config = new Configuration(event.getSuggestedConfigurationFile());
      ConfigLoader.load();
      proxy.loadInverted();
      networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("potioncore");
      networkWrapper.registerMessage(PacketHandlerServer.class, CToSMessage.class, 0, Side.SERVER);
      networkWrapper.registerMessage(PacketHandlerClient.class, SToCMessage.class, 1, Side.CLIENT);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (ConfigLoader.fixReach) {
         proxy.replaceEntityRenderer();
      }

   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      PotionCoreEffects.loadGoodBadEffects();
      PotionCoreEffects.loadInversions();
   }
}
