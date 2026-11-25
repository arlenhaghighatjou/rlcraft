package knightminer.inspirations;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.plugins.LeatherWorksPlugin;
import knightminer.inspirations.plugins.RatsPlugin;
import knightminer.inspirations.plugins.TwilightForestPlugin;
import knightminer.inspirations.plugins.tan.ToughAsNailsPlugin;
import knightminer.inspirations.plugins.top.TheOneProbePlugin;
import knightminer.inspirations.plugins.waila.WailaPlugin;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsOredict;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
   modid = "inspirations",
   name = "Inspirations",
   version = "1.12.2-0.2.9",
   dependencies = "required-after:forge@[14.23.1.2571,];required-after:mantle;after:toughasnails@[3.1.0,]",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class Inspirations {
   public static final String modID = "inspirations";
   public static final String modVersion = "1.12.2-0.2.9";
   public static final String modName = "Inspirations";
   public static final Logger log = LogManager.getLogger("inspirations");
   @Instance("inspirations")
   public static Inspirations instance;
   public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
   public static GuiHandler guiHandler = new GuiHandler();

   public Inspirations() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Config.preInit(event);
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
      InspirationsNetwork.instance.setup();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Config.init(event);
   }

   static {
      pulseManager.registerPulse(new InspirationsShared());
      pulseManager.registerPulse(new InspirationsBuilding());
      pulseManager.registerPulse(new InspirationsUtility());
      pulseManager.registerPulse(new InspirationsTools());
      pulseManager.registerPulse(new InspirationsRecipes());
      pulseManager.registerPulse(new InspirationsTweaks());
      pulseManager.registerPulse(new InspirationsOredict());
      pulseManager.registerPulse(new ToughAsNailsPlugin());
      pulseManager.registerPulse(new TheOneProbePlugin());
      pulseManager.registerPulse(new WailaPlugin());
      pulseManager.registerPulse(new LeatherWorksPlugin());
      pulseManager.registerPulse(new RatsPlugin());
      pulseManager.registerPulse(new TwilightForestPlugin());
      if (pulseManager.isPulseLoaded("InspirationsRecipes") && Config.enableCauldronFluids) {
         FluidRegistry.enableUniversalBucket();
      }

   }
}
