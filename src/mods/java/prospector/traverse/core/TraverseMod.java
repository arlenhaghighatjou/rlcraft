package prospector.traverse.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import prospector.traverse.TraverseCommon;

@Mod(
   modid = "traverse",
   name = "Traverse",
   version = "1.6.0",
   acceptedMinecraftVersions = "1.12.2"
)
public class TraverseMod {
   public static TraverseMod instance;
   public static TraverseCommon proxy;
   public static List<Item> itemModelsToRegister = new ArrayList();
   public static List<Block> blockModelsToRegister = new ArrayList();

   public TraverseMod() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new prospector.traverse.TraverseClient() : new prospector.traverse.TraverseServer();
         prospector.traverse.TraverseCommon.register(MinecraftForge.EVENT_BUS);
         prospector.traverse.world.TraverseWorld.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarting((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerAboutToStartEvent) {
         this.serverAboutToStart((FMLServerAboutToStartEvent)event);
      } else if (event instanceof FMLServerStoppingEvent) {
         this.serverStopping((FMLServerStoppingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   public void serverStarting(FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }

   public void serverAboutToStart(FMLServerAboutToStartEvent event) {
      proxy.serverAboutToStart(event);
   }

   public void serverStopping(FMLServerStoppingEvent event) {
      proxy.serverStopping(event);
   }
}
