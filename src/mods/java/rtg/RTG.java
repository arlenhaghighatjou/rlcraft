package rtg;

import java.nio.file.Paths;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import rtg.api.RTGAPI;
import rtg.api.util.PlateauUtil;
import rtg.client.GuiCustomizeWorldScreenRTG;
import rtg.event.EventHandlerCommon;
import rtg.init.BiomeInit;
import rtg.util.ModCompat;
import rtg.world.WorldTypeRTG;

@Mod(
   modid = "rtg",
   name = "Realistic Terrain Generation",
   version = "6.1.0.0-snapshot.1",
   dependencies = "required-after:forge@[14.23.5.2779,);required:rtgapi@[1.0.0,);after:abyssalcraft@[1.9.4.3,);after:biomesoplenty@[7.0.1.2399,);",
   guiFactory = "rtg.RTGConfig$RTGGuiConfigFactory",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class RTG {
   public static final String MOD_ID = "rtg";
   public static final String MOD_NAME = "Realistic Terrain Generation";
   public static final String MOD_VERSION = "6.1.0.0-snapshot.1";
   public static final String MCF_MINVER = "14.23.5.2779";
   public static final String MOD_DEPS = "after:abyssalcraft@[1.9.4.3,);after:biomesoplenty@[7.0.1.2399,);";
   private static final RTG instance = new RTG();
   private static boolean DISABLE_DECORATIONS;
   private static boolean DISABLE_SURFACES;
   @SidedProxy
   private static RTG.RTGProxy proxy;

   private RTG() {
   }

   @InstanceFactory
   public static RTG getInstance() {
      return instance;
   }

   public static RTG.RTGProxy getProxy() {
      return proxy;
   }

   @EventHandler
   public void initPre(FMLPreInitializationEvent event) {
      DISABLE_DECORATIONS = System.getProperties().containsKey("rtg.disableDecorations");
      DISABLE_SURFACES = System.getProperties().containsKey("rtg.disableSurfaces");
      RTGAPI.setConfigPath(Paths.get(event.getModConfigurationDirectory().getPath(), "rtg".toUpperCase()));
      RTGConfig.init(event);
      RTGAPI.addAllowedDimensionType(DimensionType.OVERWORLD);
      WorldTypeRTG.init();
      ModCompat.init();
      BiomeInit.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      EventHandlerCommon.init();
   }

   @EventHandler
   public void initPost(FMLPostInitializationEvent event) {
      BiomeInit.init();
      ModCompat.doBiomeCheck();
      PlateauUtil.init();
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      RTGAPI.RTG_BIOMES.setLocked();
   }

   public static boolean decorationsDisable() {
      return DISABLE_DECORATIONS;
   }

   public static boolean surfacesDisabled() {
      return DISABLE_SURFACES;
   }

   public static final class ClientProxy implements RTG.RTGProxy {
      public ClientProxy() {
      }

      public void displayCustomizeWorldScreen(GuiCreateWorld guiCreateWorld) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiCustomizeWorldScreenRTG(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
      }
   }

   public interface RTGProxy {
      void displayCustomizeWorldScreen(GuiCreateWorld var1);
   }

   public static class ServerProxy implements RTG.RTGProxy {
      public ServerProxy() {
      }

      public void displayCustomizeWorldScreen(GuiCreateWorld guiCreateWorld) {
      }
   }
}
