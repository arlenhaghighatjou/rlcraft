package tschipp.tschipplib;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tschipp.tschipplib.network.CTileEntitySyncPacket;
import tschipp.tschipplib.network.CTileEntitySyncPacketHandler;
import tschipp.tschipplib.network.STileEntitySyncPacket;
import tschipp.tschipplib.network.STileEntitySyncPacketHandler;

@Mod(modid = TschippLib.MODID, name = "TschippLib", version = TschippLib.VERSION, acceptableRemoteVersions = "[1.1.3,)")
public class TschippLib {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new tschipp.tschipplib.ClientProxy() : new tschipp.tschipplib.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

	
	public static final String MODID = "tschipplib";
	public static final String VERSION = "1.1.6";
	
	public static TschippLib instance;
	
	public static final String CLIENT_PROXY = "tschipp.tschipplib.ClientProxy";
	public static final String COMMON_PROXY = "tschipp.tschipplib.CommonProxy";

	public static Logger LOGGER = LogManager.getLogger("TschippLib");
	
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper network;

	
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("TschippLib");
		network.registerMessage(new CTileEntitySyncPacketHandler(), CTileEntitySyncPacket.class, CTileEntitySyncPacket::new, 0, Side.CLIENT);
		network.registerMessage(new STileEntitySyncPacketHandler(), STileEntitySyncPacket.class, STileEntitySyncPacket::new, 1, Side.SERVER);
	}



	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}



	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);

	}

}
