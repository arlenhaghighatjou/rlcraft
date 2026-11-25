package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.command.DTCommand;
import com.ferreusveritas.dynamictrees.compat.CompatHandler;
import com.ferreusveritas.dynamictrees.proxy.CommonProxy;
import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import com.ferreusveritas.dynamictrees.worldgen.WorldGeneratorTrees;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "dynamictrees",
   name = "Dynamic Trees",
   version = "1.12.2-0.9.29",
   dependencies = "required-after:forge@[14.23.5.2859,);before:rustic@(1.0.14,);before:dynamictreesbop@[1.12.2-1.4.3,);before:dynamictreestc@[1.12.2-1.4.2,);before:@[1.12.2-2.0.3,)@[1.12.2-2.0.3,);before:dttraverse@[1.12.2-2.0,);before:dynamictreeshnc@(1.1,);after:reccomplex",
   updateJSON = "https://github.com/DynamicTreesTeam/DynamicTreesVersionInfo/blob/master/DynamicTrees.json?raw=true",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class DynamicTrees {
   @Instance("dynamictrees")
   public static DynamicTrees instance;
   @SidedProxy(
      clientSide = "com.ferreusveritas.dynamictrees.proxy.ClientProxy",
      serverSide = "com.ferreusveritas.dynamictrees.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger log;

   public DynamicTrees() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      log = event.getModLog();
      proxy.preInit(event);
      CompatHandler.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      proxy.cleanUp();
   }

   @EventHandler
   public static void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new DTCommand());
      SeasonHelper.getSeasonManager().flushMappings();
   }

   @EventHandler
   public static void serverStopped(FMLServerStoppedEvent event) {
      WorldGeneratorTrees.clearFlatWorldCache();
   }
}
