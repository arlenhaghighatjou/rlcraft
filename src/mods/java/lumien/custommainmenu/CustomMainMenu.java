package lumien.custommainmenu;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import lumien.custommainmenu.configuration.Config;
import lumien.custommainmenu.configuration.ConfigurationLoader;
import lumien.custommainmenu.handler.CMMEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

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
   public static CustomMainMenu INSTANCE;
   public static CMMEventHandler EVENT_HANDLER;
   private ConfigurationLoader configLoader;
   public Config config;
   public Logger logger;
   public VFile2 configFolder;
   static ResourceLocation transparentTexture;

   public CustomMainMenu() {
   }

   public static void bindTransparent() {
      Minecraft.getMinecraft().renderEngine.bindTexture(transparentTexture);
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      INSTANCE = this;
      this.configFolder = event.getModConfigurationDirectory();
      this.config = new Config();
      transparentTexture = new ResourceLocation("custommainmenu:textures/gui/transparent.png");
      EVENT_HANDLER = new CMMEventHandler();
      EVENT_HANDLER.register(MinecraftForge.EVENT_BUS);
      this.logger = event.getModLog();
      this.configLoader = new ConfigurationLoader(this.config);

      try {
         this.configLoader.load();
      } catch (Exception var3) {
         this.logger.error("Could not load the custom menu configuration", var3);
         this.config = new Config();
      }
   }

   public void reload() {
      Config backup = this.config;
      this.config = new Config();
      this.configLoader = new ConfigurationLoader(this.config);

      try {
         this.configLoader.load();
         EVENT_HANDLER.displayMs = -1L;
      } catch (Exception var3) {
         EVENT_HANDLER.displayMs = EagRuntime.steadyTimeMillis();
         this.logger.error("Could not reload the custom menu configuration", var3);
         this.config = backup;
      }

   }
}
