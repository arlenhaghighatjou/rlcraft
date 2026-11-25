package fionathemortal.betterbiomeblend;

import fionathemortal.betterbiomeblend.core.CoreMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "betterbiomeblend",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public class BetterBiomeBlend {
   public static final String MOD_ID = "betterbiomeblend";
   public static final String MOD_NAME = "Better Biome Blend";
   public static final Logger LOGGER = LogManager.getLogger("betterbiomeblend");

   public BetterBiomeBlend() {
   }

   @EventHandler
   public void onPreInit(FMLPreInitializationEvent event) {
      if (!CoreMod.foundMixinFramework) {
         MinecraftForge.EVENT_BUS.register(new MissingMixinFrameworkHandler());
      }

   }
}
