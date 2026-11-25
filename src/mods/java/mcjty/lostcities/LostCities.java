package mcjty.lostcities;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import java.util.function.Function;
import mcjty.lostcities.api.ILostCities;
import mcjty.lostcities.commands.CommandBuildPart;
import mcjty.lostcities.commands.CommandDebug;
import mcjty.lostcities.commands.CommandExportBuilding;
import mcjty.lostcities.commands.CommandExportPart;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import mcjty.lostcities.dimensions.world.lost.BiomeInfo;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import mcjty.lostcities.dimensions.world.lost.City;
import mcjty.lostcities.dimensions.world.lost.CitySphere;
import mcjty.lostcities.dimensions.world.lost.Highway;
import mcjty.lostcities.dimensions.world.lost.Railway;
import mcjty.lostcities.setup.IProxy;
import mcjty.lostcities.setup.ModSetup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

@Mod(
   modid = "lostcities",
   name = "The Lost Cities",
   dependencies = "after:forge@[13.19.0.2176,)",
   version = "2.0.22",
   acceptedMinecraftVersions = "[1.12,1.13)",
   acceptableRemoteVersions = "*"
)
public class LostCities {
   public static final String MODID = "lostcities";
   public static final String VERSION = "2.0.22";
   public static final String MIN_FORGE11_VER = "13.19.0.2176";
   @SidedProxy(
      clientSide = "mcjty.lostcities.setup.ClientProxy",
      serverSide = "mcjty.lostcities.setup.ServerProxy"
   )
   public static IProxy proxy;
   public static ModSetup setup = new ModSetup();
   @Instance("lostcities")
   public static LostCities instance;
   public static LostCitiesImp lostCitiesImp = new LostCitiesImp();

   public LostCities() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      setup.preInit(e);
      proxy.preInit(e);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
      setup.init(e);
      proxy.init(e);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      setup.postInit(e);
      proxy.postInit(e);
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandDebug());
      event.registerServerCommand(new CommandExportBuilding());
      event.registerServerCommand(new CommandExportPart());
      event.registerServerCommand(new CommandBuildPart());
      this.cleanCaches();
   }

   @EventHandler
   public void serverStopped(FMLServerStoppedEvent event) {
      this.cleanCaches();
      WorldTypeTools.cleanChunkGeneratorMap();
   }

   private void cleanCaches() {
      BuildingInfo.cleanCache();
      Highway.cleanCache();
      Railway.cleanCache();
      BiomeInfo.cleanCache();
      City.cleanCache();
      CitySphere.cleanCache();
      WorldTypeTools.cleanCache();
   }

   @EventHandler
   public void imcCallback(IMCEvent event) {
      UnmodifiableIterator var2 = event.getMessages().iterator();

      while(var2.hasNext()) {
         IMCMessage message = (IMCMessage)var2.next();
         if (message.key.equalsIgnoreCase("getLostCities")) {
            Optional<Function<ILostCities, Void>> value = message.getFunctionValue(ILostCities.class, Void.class);
            if (value.isPresent()) {
               ((Function)value.get()).apply(lostCitiesImp);
            } else {
               setup.getLogger().warn("Some mod didn't return a valid result with getLostCities!");
            }
         }
      }

   }
}
