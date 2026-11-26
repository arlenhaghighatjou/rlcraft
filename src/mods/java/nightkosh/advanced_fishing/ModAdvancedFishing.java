package nightkosh.advanced_fishing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nightkosh.advanced_fishing.api.AdvancedFishingAPI;
import nightkosh.advanced_fishing.core.CatchManager;
import nightkosh.advanced_fishing.core.Config;
import nightkosh.advanced_fishing.core.Entity;
import nightkosh.advanced_fishing.core.EventsHandler;
import nightkosh.advanced_fishing.core.LootTables;
import nightkosh.advanced_fishing.core.MaterialManager;
import nightkosh.advanced_fishing.core.ParticlesManager;
import nightkosh.advanced_fishing.core.Recipes;
import nightkosh.advanced_fishing.core.Tabs;
import nightkosh.advanced_fishing.proxy.CommonProxy;

@Mod(
   modid = "advanced-fishing",
   name = "Advanced-Fishing",
   version = "1.3.3",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ModAdvancedFishing {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         INSTANCE = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new nightkosh.advanced_fishing.proxy.ClientProxy() : new nightkosh.advanced_fishing.proxy.CommonProxy();
         nightkosh.advanced_fishing.core.Items.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         nightkosh.advanced_fishing.core.Models.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.load((FMLInitializationEvent)event);
      }

   }

   public static ModAdvancedFishing INSTANCE;
   public static CommonProxy proxy;

   public ModAdvancedFishing() {
      INSTANCE = this;
      AdvancedFishingAPI.PARTICLES_MANAGER = ParticlesManager.INSTANCE;
      AdvancedFishingAPI.CATCH_MANAGER = CatchManager.INSTANCE;
      AdvancedFishingAPI.MATERIAL_MANAGER = MaterialManager.INSTANCE;
      AdvancedFishingAPI.FISH_TAB = Tabs.FISH_TAB;
   }

   public void preInit(FMLPreInitializationEvent event) {
      (new Config(event.getSuggestedConfigurationFile())).getConfigs();
      Entity.registration();
      LootTables.registration();
   }

   public void load(FMLInitializationEvent event) {
      (new EventsHandler()).register(MinecraftForge.EVENT_BUS);
      Recipes.smeltingRecipesRegistration();
      Recipes.addLuckPotionRecipe();
      proxy.registerMobsRenderers();
   }
}
