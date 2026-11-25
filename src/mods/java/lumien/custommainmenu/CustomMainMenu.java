package lumien.custommainmenu;

import java.io.File;
import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.ConfigurationLoader;
import lumien.custommainmenu.handler.CMMEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "custommainmenu",
   name = "Custom Main Menu",
   version = "2.0.9.1",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public class CustomMainMenu {
   public static final String MOD_ID = "custommainmenu";
   public static final String MOD_NAME = "Custom Main Menu";
   public static final String MOD_VERSION = "2.0.9.1";
   @Instance("custommainmenu")
   public static CustomMainMenu INSTANCE;
   public static CMMEventHandler EVENT_HANDLER;
   private ConfigurationLoader configLoader;
   public Config config;
   public Logger logger;
   public File configFolder;
   static ResourceLocation transparentTexture;

   public CustomMainMenu() {
   }

   public static void bindTransparent() {
      Minecraft.getMinecraft().renderEngine.bindTexture(transparentTexture);
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.configFolder = event.getModConfigurationDirectory();
      this.config = new Config();
      transparentTexture = new ResourceLocation("custommainmenu:textures/gui/transparent.png");
      EVENT_HANDLER = new CMMEventHandler();
      MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
      FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
      this.logger = event.getModLog();
      this.configLoader = new ConfigurationLoader(this.config);

      try {
         this.configLoader.load();
      } catch (Exception var3) {
         this.logger.log(Level.ERROR, "Error while loading config file. Will have to crash here :(.");
         throw new RuntimeException(var3);
      }
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }

   public void reload() {
      Config backup = this.config;
      this.config = new Config();
      this.configLoader = new ConfigurationLoader(this.config);

      try {
         this.configLoader.load();
         EVENT_HANDLER.displayMs = -1L;
      } catch (Exception var3) {
         var3.printStackTrace();
         EVENT_HANDLER.displayMs = System.currentTimeMillis();
         this.logger.log(Level.ERROR, "Error while loading new config file, trying to keep the old one loaded.");
         this.config = backup;
      }

   }
}
