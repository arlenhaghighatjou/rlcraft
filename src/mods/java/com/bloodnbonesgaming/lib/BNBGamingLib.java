package com.bloodnbonesgaming.lib;

import com.bloodnbonesgaming.lib.command.BNBCommand;
import com.bloodnbonesgaming.lib.proxy.CommonProxy;
import com.bloodnbonesgaming.lib.util.ModManager;
import com.bloodnbonesgaming.lib.util.SaveHelper;
import com.bloodnbonesgaming.lib.util.StringHelper;
import java.io.File;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "bnbgaminglib",
   name = "BNBGamingLib",
   version = "2.17.6",
   acceptableRemoteVersions = "*",
   dependencies = "after:bnbgamingcore",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class BNBGamingLib extends BNBGamingMod {
   @Instance("bnbgaminglib")
   public static BNBGamingLib libInstance;
   @SidedProxy(
      clientSide = "com.bloodnbonesgaming.lib.proxy.ClientProxy",
      serverSide = "com.bloodnbonesgaming.lib.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   public BNBGamingLib() {
   }

   @EventHandler
   public void onPreInit(FMLPreInitializationEvent e) {
      proxy.registerEventHandlers();
   }

   @EventHandler
   public void onLoadComplete(FMLLoadCompleteEvent e) {
      this.log.info(StringHelper.build("FML mod loading complete. There appear to be ", ModManager.getRegisteredMods().size(), " BNBGaming mods present."));
   }

   @EventHandler
   public void serverAboutToStart(FMLServerAboutToStartEvent event) {
      SaveHelper.setServerSaveFolder(new File(FMLCommonHandler.instance().getSavesDirectory(), event.getServer().getFolderName()));
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new BNBCommand());
      proxy.registerCommands(event);
   }
}
