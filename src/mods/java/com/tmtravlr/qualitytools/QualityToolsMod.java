package com.tmtravlr.qualitytools;

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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

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
   @Instance
   public static QualityToolsMod instance;
   @SidedProxy(
      clientSide = "com.tmtravlr.qualitytools.ClientProxy",
      serverSide = "com.tmtravlr.qualitytools.CommonProxy"
   )
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

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      ConfigLoader.loadPreInit(event);
      logger = event.getModLog();
      networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("qualitytools");
      networkWrapper.registerMessage(PacketHandlerServer.class, CToSMessage.class, 0, Side.SERVER);
      networkWrapper.registerMessage(PacketHandlerClient.class, SToCMessage.class, 1, Side.CLIENT);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.registerRenderers();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new QualityToolsGuiHandler());
   }

   @EventHandler
   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandQualityToolsReload());
      ConfigLoader.reloadConfigs();
   }
}
