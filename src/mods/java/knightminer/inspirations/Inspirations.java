package knightminer.inspirations;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import slimeknights.mantle.pulsar.pulse.PulseInfo;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.plugins.LeatherWorksPlugin;
import knightminer.inspirations.plugins.RatsPlugin;
import knightminer.inspirations.plugins.TwilightForestPlugin;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsOredict;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
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
   public static Inspirations instance;
   public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
   public static GuiHandler guiHandler = new GuiHandler();

   public Inspirations() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "texture_recipe"), new knightminer.inspirations.library.recipe.TextureRecipe.Factory());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "shapeless_no_container"), new knightminer.inspirations.library.recipe.ShapelessNoContainerRecipe.Factory());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "waypoint_compass"), new knightminer.inspirations.tools.recipe.WaypointCompassDyeingRecipe.Factory());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "pulse_loaded"), new knightminer.inspirations.common.Config.PulseLoaded());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "config"), new knightminer.inspirations.common.Config.ConfigProperty());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("inspirations", "mod_item_list"), new knightminer.inspirations.library.recipe.ModItemList.Factory());
         knightminer.inspirations.plugins.LeatherWorksPlugin.registerHolders();
         knightminer.inspirations.plugins.RatsPlugin.registerHolders();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

      pulseManager.propagateEvent(event);
   }

   public void preInit(FMLPreInitializationEvent event) {
      Config.preInit(event);
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
      InspirationsNetwork.instance.setup();
   }

   public void init(FMLInitializationEvent event) {
      Config.init(event);
   }

   static {
      pulseManager.registerPulse(new InspirationsShared(), new PulseInfo("InspirationsShared", "Blocks and items used by all modules", "", "", true, true));
      pulseManager.registerPulse(new InspirationsBuilding(), new PulseInfo("InspirationsBuilding", "A collection of features to improve building", "", "", false, true));
      pulseManager.registerPulse(new InspirationsUtility(), new PulseInfo("InspirationsUtility", "Adds various utilities", "", "", false, true));
      pulseManager.registerPulse(new InspirationsTools(), new PulseInfo("InspirationsTools", "Adds various tools or tweaks to vanilla tools", "", "", false, true));
      pulseManager.registerPulse(new InspirationsRecipes(), new PulseInfo("InspirationsRecipes", "Adds additional recipe types, including cauldrons and anvil smashing", "", "", false, true));
      pulseManager.registerPulse(new InspirationsTweaks(), new PulseInfo("InspirationsTweaks", "Various vanilla tweaks", "", "", false, true));
      pulseManager.registerPulse(new InspirationsOredict(), new PulseInfo("InspirationsShared", "Blocks and items used by all modules", "", "", true, true));
      pulseManager.registerPulse(new LeatherWorksPlugin(), new PulseInfo("LeatherWorks", "Adds recipes for Leather Works items", "leatherworks", "InspirationsRecipes", false, true));
      pulseManager.registerPulse(new RatsPlugin(), new PulseInfo("RatsPlugin", "Supports the Rats milk cauldron as part of cauldron recipes. Note extended features such as recipes from dropping items in cauldrons will now work.", "rats", "InspirationsRecipes", false, true));
      pulseManager.registerPulse(new TwilightForestPlugin(), new PulseInfo("TwilightForestPlugin", "Supports dyeing Twilight Forest armor in the Inspirations cauldron.", "twilightforest", "InspirationsRecipes", false, true));
      if (pulseManager.isPulseLoaded("InspirationsRecipes") && Config.enableCauldronFluids) {
         FluidRegistry.enableUniversalBucket();
      }

   }
}
