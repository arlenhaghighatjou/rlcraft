package materialtweaker.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import materialtweaker.handlers.CustomConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "materialtweaker",
   version = "1.1.1",
   name = "MaterialTweaker"
)
public class MaterialTweaker {
   public static final String MODID = "materialtweaker";
   public static final String VERSION = "1.1.1";
   public static final String NAME = "MaterialTweaker";
   public static final Logger LOGGER = LogManager.getLogger();
   public static MaterialTweaker instance;

   public MaterialTweaker() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         materialtweaker.handlers.ForgeConfigHandler.register();
         materialtweaker.handlers.ForgeConfigHandler.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPostInitializationEvent) {
         MaterialTweaker.postInitialization((FMLPostInitializationEvent)event);
      }

   }

   public static void postInitialization(FMLPostInitializationEvent e) {
      CustomConfigHandler.forceInitializeLazyFiles();
   }
}
