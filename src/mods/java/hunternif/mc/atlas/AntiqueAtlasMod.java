package hunternif.mc.atlas;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import hunternif.mc.atlas.core.AtlasDataHandler;
import hunternif.mc.atlas.core.PlayerEventHandler;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.watcher.DeathWatcher;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherFortress;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherGeneric;
import hunternif.mc.atlas.ext.watcher.impl.StructureWatcherVillage;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import hunternif.mc.atlas.marker.MarkersDataHandler;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.registry.MarkerTypes;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "antiqueatlas",
   name = "Antique Atlas",
   version = "4.6.3",
   dependencies = "after:forge@[14.23.2.2611,);",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class AntiqueAtlasMod {
   public static final String ID = "antiqueatlas";
   public static final String NAME = "Antique Atlas";
   public static final String CHANNEL = "antiqueatlas";
   public static final String VERSION = "4.6.3";
   public static AntiqueAtlasMod instance;
   public boolean jeidPresent = false;
   public static CommonProxy proxy;
   public static final AtlasDataHandler atlasData = new AtlasDataHandler();
   public static final MarkersDataHandler markersData = new MarkersDataHandler();
   public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
   public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();

   public AntiqueAtlasMod() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new hunternif.mc.atlas.ClientProxy() : new hunternif.mc.atlas.CommonProxy();
         hunternif.mc.atlas.SettingsConfig.register();
         hunternif.mc.atlas.RegistrarAntiqueAtlas.registerHolders();
         hunternif.mc.atlas.api.AtlasAPI.registerHolders();
         hunternif.mc.atlas.RegistrarAntiqueAtlas.register(MinecraftForge.EVENT_BUS);
         hunternif.mc.atlas.SettingsConfig.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      PacketDispatcher.registerPackets();
      proxy.init(event);
      if (!SettingsConfig.gameplay.itemNeeded) {
         (new PlayerEventHandler()).register(MinecraftForge.EVENT_BUS);
      }

      atlasData.register(MinecraftForge.EVENT_BUS);
      markersData.register(MinecraftForge.EVENT_BUS);
      extBiomeData.register(MinecraftForge.EVENT_BUS);
      globalMarkersData.register(MinecraftForge.EVENT_BUS);
      (new DeathWatcher()).register(MinecraftForge.EVENT_BUS);
      (new NetherPortalWatcher()).register(MinecraftForge.EVENT_BUS);
      (new StructureWatcherVillage()).register(MinecraftForge.EVENT_BUS);
      new StructureWatcherFortress();
      (new StructureWatcherGeneric("EndCity", DimensionType.THE_END, MarkerTypes.END_CITY_FAR, "gui.antiqueatlas.marker.endcity")).setTileMarker(MarkerTypes.END_CITY, "gui.antiqueatlas.marker.endcity");
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }
}
