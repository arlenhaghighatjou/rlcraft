package kenkron.antiqueatlasoverlay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "antiqueatlasoverlay",
   version = "1.2",
   name = "antiqueatlasoverlay",
   clientSideOnly = true,
   dependencies = "required-after:antiqueatlas;after:forge@[13.20.0.2262,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class AntiqueAtlasOverlayMod {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         kenkron.antiqueatlasoverlay.AAOConfig.register();
         kenkron.antiqueatlasoverlay.AAOConfig.register(MinecraftForge.EVENT_BUS);
         kenkron.antiqueatlasoverlay.AAORenderEventReceiver.register(MinecraftForge.EVENT_BUS);
      }

   }

   public static final String MODID = "antiqueatlasoverlay";
   public static final String VERSION = "1.2";
   public static final Logger LOGGER = LogManager.getLogger("AntiqueAtlasOverlay");

   public AntiqueAtlasOverlayMod() {
   }
}
