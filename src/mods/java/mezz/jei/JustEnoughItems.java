package mezz.jei;

import java.util.Map;
import mezz.jei.config.ServerInfo;
import mezz.jei.startup.ProxyCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
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
   @SidedProxy(
      clientSide = "mezz.jei.startup.ProxyCommonClient",
      serverSide = "mezz.jei.startup.ProxyCommon"
   )
   private static ProxyCommon proxy;

   public JustEnoughItems() {
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

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      proxy.loadComplete(event);
   }
}
