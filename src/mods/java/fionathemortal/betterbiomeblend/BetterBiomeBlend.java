package fionathemortal.betterbiomeblend;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

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

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         fionathemortal.betterbiomeblend.BetterBiomeBlendConfig.register();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            fionathemortal.betterbiomeblend.BetterBiomeBlendClient.register(MinecraftForge.EVENT_BUS);
         }
      }

   }

}
