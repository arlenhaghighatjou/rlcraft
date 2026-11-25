package nightkosh.advanced_fishing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("advanced-fishing")
   public static ModAdvancedFishing INSTANCE;
   @SidedProxy(
      clientSide = "nightkosh.advanced_fishing.proxy.ClientProxy",
      serverSide = "nightkosh.advanced_fishing.proxy.CommonProxy"
   )
   public static CommonProxy proxy;

   public ModAdvancedFishing() {
      INSTANCE = this;
      AdvancedFishingAPI.PARTICLES_MANAGER = ParticlesManager.INSTANCE;
      AdvancedFishingAPI.CATCH_MANAGER = CatchManager.INSTANCE;
      AdvancedFishingAPI.MATERIAL_MANAGER = MaterialManager.INSTANCE;
      AdvancedFishingAPI.FISH_TAB = Tabs.FISH_TAB;
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      (new Config(event.getSuggestedConfigurationFile())).getConfigs();
      Entity.registration();
      LootTables.registration();
   }

   @EventHandler
   public void load(FMLInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(new EventsHandler());
      Recipes.smeltingRecipesRegistration();
      Recipes.addLuckPotionRecipe();
      proxy.registerMobsRenderers();
   }
}
