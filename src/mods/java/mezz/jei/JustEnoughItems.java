package mezz.jei;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.Map;
import mezz.jei.config.ServerInfo;
import mezz.jei.startup.ProxyCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "jei",
   name = "Just Enough Items",
   version = "4.16.1.302",
   guiFactory = "mezz.jei.config.JEIModGuiFactory",
   acceptedMinecraftVersions = "[1.12.2,1.13)",
   dependencies = "required-after:forge@[14.23.5.2816,);"
)
public class JustEnoughItems {
   private static ProxyCommon proxy;

   public JustEnoughItems() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new mezz.jei.startup.ProxyCommonClient() : new mezz.jei.startup.ProxyCommon();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            mezz.jei.input.MouseHelper.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.loadComplete((FMLLoadCompleteEvent)event);
      }

   }

   public static ProxyCommon getProxy() {
      return proxy;
   }

   @NetworkCheckHandler
   public boolean checkModLists(Map<String, String> modList, Side side) {
      if (side == Side.SERVER) {
         boolean jeiOnServer = modList.containsKey("jei");
         ServerInfo.onConnectedToServer(jeiOnServer);
      }

      return true;
   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
      proxy.loadComplete(event);
   }
}
