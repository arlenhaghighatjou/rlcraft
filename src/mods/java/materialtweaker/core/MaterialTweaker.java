package materialtweaker.core;

import materialtweaker.handlers.CustomConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("materialtweaker")
   public static MaterialTweaker instance;

   public MaterialTweaker() {
   }

   @EventHandler
   public static void postInitialization(FMLPostInitializationEvent e) {
      CustomConfigHandler.forceInitializeLazyFiles();
   }
}
