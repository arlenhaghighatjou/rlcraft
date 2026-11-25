package collisiondamage.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLEvent;
import collision.handlers.PacketHandler;
import collisiondamage.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "collisiondamage",
   version = "1.2.2",
   name = "CollisionDamage"
)
public class CollisionDamage {
   public static final String MODID = "collisiondamage";
   public static final String VERSION = "1.2.2";
   public static final String NAME = "CollisionDamage";
   public static final String PROXY = "collisiondamage.core.proxies";
   public static final String CHANNEL = "COLLISIONDAMAGE";
   public static CollisionDamage instance;
   public static CommonProxy proxy;
   public static Logger logger;

   public CollisionDamage() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new collisiondamage.core.proxies.ClientProxy() : new collisiondamage.core.proxies.CommonProxy();
         collision.handlers.ModConfig.register();
         collision.handlers.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.onInit((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      PacketHandler.registerPacket();
   }

   public void onInit(FMLInitializationEvent event) {
      proxy.registerHandlers();
   }
}
