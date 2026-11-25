package prospector.traverse.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("traverse")
   public static TraverseMod instance;
   @SidedProxy(
      clientSide = "prospector.traverse.TraverseClient",
      serverSide = "prospector.traverse.TraverseServer"
   )
   public static TraverseCommon proxy;
   public static List<Item> itemModelsToRegister = new ArrayList();
   public static List<Block> blockModelsToRegister = new ArrayList();

   public TraverseMod() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }

   @EventHandler
   public void serverAboutToStart(FMLServerAboutToStartEvent event) {
      proxy.serverAboutToStart(event);
   }

   @EventHandler
   public void serverStopping(FMLServerStoppingEvent event) {
      proxy.serverStopping(event);
   }
}
