package com.charles445.rltweaker.config;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(
   modid = "rltweaker"
)
public class ModConfig {
   @Comment({"Client options"})
   @Name("Client")
   public static ModConfig.ClientConfig client = new ModConfig.ClientConfig();
   @Comment({"Patch options for the coremod"})
   @Name("patches")
   public static PatchConfig patches = new PatchConfig();
   @Comment({"Server options"})
   @Name("Server")
   public static ModConfig.ServerConfig server = new ModConfig.ServerConfig();

   public ModConfig() {
   }

   public static void onConfigChanged() {
      load(server.potioncore.incurablePotionEffectsImpl, server.potioncore.incurablePotionEffects, ResourceLocation::new);
      load(server.potioncore.cureDisablingPotionEffectsImpl, server.potioncore.cureDisablingPotionEffects, ResourceLocation::new);
   }

   private static <T> void load(Collection<T> collection, String[] data, Function<String, T> parser) {
      collection.clear();
      Stream.of(data).map(parser).forEach(collection::add);
   }

   public static class ClientConfig {
      @Comment({"Classy Hats tweaks"})
      @Name("Classy Hats")
      public ConfigClassyHatsClient classyhats = new ConfigClassyHatsClient();
      @Comment({"Fancy Block Particles tweaks"})
      @Name("Fancy Block Particles")
      public ConfigFBPClient fbp = new ConfigFBPClient();
      @Comment({"Googly Eyes tweaks"})
      @Name("Googly Eyes")
      public ConfigGooglyEyesClient googlyeyes = new ConfigGooglyEyesClient();
      @Comment({"Mantle tweaks"})
      @Name("Mantle")
      public ConfigMantleClient mantle = new ConfigMantleClient();
      @Comment({"Minecraft tweaks"})
      @Name("Minecraft")
      public ConfigMinecraftClient minecraft = new ConfigMinecraftClient();
      @Comment({"Potion Core tweaks"})
      @Name("Potion Core")
      public ConfigPotionCoreClient potioncore = new ConfigPotionCoreClient();
      @Comment({"Reskillable tweaks"})
      @Name("Reskillable")
      public ConfigReskillableClient reskillable = new ConfigReskillableClient();

      public ClientConfig() {
      }
   }

   @EventBusSubscriber(
      modid = "rltweaker"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      @SideOnly(Side.CLIENT)
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("rltweaker")) {
            ConfigManager.sync("rltweaker", Type.INSTANCE);
            ModConfig.onConfigChanged();
         }

      }
   }

   public static class ServerConfig {
      @Comment({"Minecraft tweaks, or anything that isn't mod specific"})
      @Name("Minecraft")
      public ConfigMinecraft minecraft = new ConfigMinecraft();
      @Comment({"Aquaculture tweaks"})
      @Name("Aquaculture")
      public ConfigAquaculture aquaculture = new ConfigAquaculture();
      @Comment({"Battle Towers tweaks"})
      @Name("Battle Towers")
      public ConfigBattleTowers battletowers = new ConfigBattleTowers();
      @Comment({"Baubles tweaks"})
      @Name("Baubles")
      public ConfigBaubles baubles = new ConfigBaubles();
      @Comment({"Better Survival tweaks"})
      @Name("Better Survival")
      public ConfigBetterSurvival bettersurvival = new ConfigBetterSurvival();
      @Comment({"Carry On tweaks"})
      @Name("Carry On")
      public ConfigCarryOn carryon = new ConfigCarryOn();
      @Comment({"Charm tweaks"})
      @Name("Charm")
      public ConfigCharm charm = new ConfigCharm();
      @Comment({"Classy Hats tweaks"})
      @Name("Classy Hats")
      public ConfigClassyHats classyhats = new ConfigClassyHats();
      @Comment({"Dynamic Surroundings tweaks"})
      @Name("Dynamic Surroundings")
      public ConfigDynamicSurroundings dynamicsurroundings = new ConfigDynamicSurroundings();
      @Comment({"Grappling Hook Mod tweaks"})
      @Name("Grappling Hook Mod")
      public ConfigGrapplemod grapplemod = new ConfigGrapplemod();
      @Comment({"Ice and Fire tweaks"})
      @Name("Ice and Fire")
      public ConfigIceAndFire iceandfire = new ConfigIceAndFire();
      @Comment({"Infernal Mobs tweaks"})
      @Name("Infernal Mobs")
      public ConfigInfernalMobs infernalmobs = new ConfigInfernalMobs();
      @Comment({"Level Up Reloaded tweaks"})
      @Name("Level Up Reloaded")
      public ConfigLevelUpTwo leveluptwo = new ConfigLevelUpTwo();
      @Comment({"Lost Cities tweaks"})
      @Name("Lost Cities")
      public ConfigLostCities lostcities = new ConfigLostCities();
      @Comment({"Lycanites Mobs tweaks"})
      @Name("Lycanites Mobs")
      public ConfigLycanites lycanitesmobs = new ConfigLycanites();
      @Comment({"Multi Mine tweaks"})
      @Name("Multi Mine")
      public ConfigMultiMine multimine = new ConfigMultiMine();
      @Comment({"Quark tweaks"})
      @Name("Quark")
      public ConfigQuark quark = new ConfigQuark();
      @Comment({"Potion Core tweaks"})
      @Name("Potion Core")
      public ConfigPotionCore potioncore = new ConfigPotionCore();
      @Comment({"Recurrent Complex tweaks"})
      @Name("Recurrent Complex")
      public ConfigRecurrent recurrentcomplex = new ConfigRecurrent();
      @Comment({"Reskillable tweaks"})
      @Name("Reskillable")
      public ConfigReskillable reskillable = new ConfigReskillable();
      @Comment({"Roguelike Dungeons tweaks"})
      @Name("Roguelike Dungeons")
      public ConfigRoguelike roguelike = new ConfigRoguelike();
      @Comment({"Ruins tweaks"})
      @Name("Ruins")
      public ConfigRuins ruins = new ConfigRuins();
      @Comment({"Rustic tweaks"})
      @Name("Rustic")
      public ConfigRustic rustic = new ConfigRustic();
      @Comment({"Serene Seasons tweaks"})
      @Name("Serene Seasons")
      public ConfigSereneSeasons sereneseasons = new ConfigSereneSeasons();
      @Comment({"Scape and Run Parasites tweaks"})
      @Name("Scape and Run Parasites")
      public ConfigSRParasites srparasites = new ConfigSRParasites();
      @Comment({"So Many Enchantments tweaks"})
      @Name("So Many Enchantments")
      public ConfigSME somanyenchantments = new ConfigSME();
      @Comment({"Spawner Control tweaks"})
      @Name("Spawner Control")
      public ConfigSpawnerControl spawnercontrol = new ConfigSpawnerControl();
      @Comment({"Tough As Nails tweaks"})
      @Name("Tough As Nails")
      public ConfigTAN toughasnails = new ConfigTAN();
      @Comment({"Waystones tweaks"})
      @Name("Waystones")
      public ConfigWaystones waystones = new ConfigWaystones();

      public ServerConfig() {
      }
   }
}
