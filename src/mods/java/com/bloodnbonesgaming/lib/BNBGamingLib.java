package com.bloodnbonesgaming.lib;

import com.bloodnbonesgaming.lib.command.BNBCommand;
import com.bloodnbonesgaming.lib.network.NetworkManager;
import com.bloodnbonesgaming.lib.proxy.ClientProxy;
import com.bloodnbonesgaming.lib.proxy.CommonProxy;
import com.bloodnbonesgaming.lib.proxy.ServerProxy;
import com.bloodnbonesgaming.lib.util.ModManager;
import com.bloodnbonesgaming.lib.util.SaveHelper;
import com.bloodnbonesgaming.lib.util.StringHelper;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveFormat;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "bnbgaminglib",
   name = "BNBGamingLib",
   version = "2.17.6",
   acceptableRemoteVersions = "*",
   dependencies = "after:bnbgamingcore",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class BNBGamingLib extends BNBGamingMod {
   public static BNBGamingLib libInstance;
   public static CommonProxy proxy;
   public final NetworkManager networkManager = NetworkManager.INSTANCE;

   public BNBGamingLib() {
      super("bnbgaminglib");
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         libInstance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new ClientProxy() : new ServerProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         onPreInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         onLoadComplete((FMLLoadCompleteEvent)event);
      } else if (event instanceof FMLServerAboutToStartEvent) {
         serverAboutToStart((FMLServerAboutToStartEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         serverStarting((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerStoppedEvent) {
         SaveHelper.setServerSaveFolder(null);
      }
   }

   public void onPreInit(FMLPreInitializationEvent e) {
      proxy.registerEventHandlers();
   }

   public void onLoadComplete(FMLLoadCompleteEvent e) {
      this.log.info(StringHelper.build("FML mod loading complete. There appear to be ", ModManager.getRegisteredMods().size(), " BNBGaming mods present."));
   }

   public void serverAboutToStart(FMLServerAboutToStartEvent event) {
      SaveHelper.setServerSaveFolder(WorldsDB.newVFile(EaglerSaveFormat.worldsFolder, event.getServer().getFolderName()));
   }

   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new BNBCommand());
   }
}
