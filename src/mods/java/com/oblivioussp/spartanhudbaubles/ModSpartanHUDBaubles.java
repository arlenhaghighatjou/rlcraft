package com.oblivioussp.spartanhudbaubles;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.oblivioussp.spartanhudbaubles.util.Config;
import com.oblivioussp.spartanhudbaubles.util.Log;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "spartanhudbaubles",
   name = "Spartan HUD: Baubles",
   version = "1.0.0 Indev 1",
   dependencies = "required-after:baubles",
   acceptedMinecraftVersions = "[1.12.2]",
   clientSideOnly = true,
   guiFactory = "com.oblivioussp.spartanhudbaubles.client.gui.GuiFactorySHB"
)
public class ModSpartanHUDBaubles {
   public static final String ID = "spartanhudbaubles";
   public static final String NAME = "Spartan HUD: Baubles";
   public static final String VERSION = "1.0.0 Indev 1";
   public static final String DEPENDENCIES = "required-after:baubles";
   public static final String MC_VERSION = "[1.12.2]";
   public static final String GUI_FACTORY_CLASS = "com.oblivioussp.spartanhudbaubles.client.gui.GuiFactorySHB";
   public static ModSpartanHUDBaubles INSTANCE;

   public ModSpartanHUDBaubles() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         INSTANCE = this;
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.oblivioussp.spartanhudbaubles.client.gui.HudElementBaubles.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         ModSpartanHUDBaubles.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         ModSpartanHUDBaubles.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         ModSpartanHUDBaubles.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static void preInit(FMLPreInitializationEvent ev) {
      Config.init(ev.getModConfigurationDirectory());
      (new Config()).register(MinecraftForge.EVENT_BUS);
      Log.info("Finished preInit phase!");
   }

   public static void init(FMLInitializationEvent ev) {
      Log.info("Finished init phase!");
   }

   public static void postInit(FMLPostInitializationEvent ev) {
      Log.info("Finished postInit phase!");
   }
}
