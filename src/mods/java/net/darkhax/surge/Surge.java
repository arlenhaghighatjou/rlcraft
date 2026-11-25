package net.darkhax.surge;

import net.darkhax.surge.core.SurgeConfiguration;
import net.darkhax.surge.core.SurgeLoadingPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "surge",
   name = "Surge",
   version = "2.0.79",
   acceptableRemoteVersions = "*",
   certificateFingerprint = "d476d1b22b218a10d845928d1665d45fce301b27",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Surge {
   public static final Logger LOG = SurgeLoadingPlugin.LOG;

   public Surge() {
   }

   @EventHandler
   public void onLoadComplete(FMLLoadCompleteEvent event) {
      if (SurgeConfiguration.showTotalLoadtime) {
         LOG.info("The game loaded in approximately  {} seconds.", Float.valueOf((float)(System.currentTimeMillis() - SurgeLoadingPlugin.firstLaunchTime) / 1000.0F));
      }

   }
}
