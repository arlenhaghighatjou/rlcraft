package org.orecruncher.dsurround;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.registry.config.Profiles;
import org.orecruncher.lib.ConfigProcessor;
import org.orecruncher.lib.ConfigProcessor.Category;
import org.orecruncher.lib.ConfigProcessor.Comment;
import org.orecruncher.lib.ConfigProcessor.DefaultValue;
import org.orecruncher.lib.ConfigProcessor.Hidden;
import org.orecruncher.lib.ConfigProcessor.LangKey;
import org.orecruncher.lib.ConfigProcessor.Option;
import org.orecruncher.lib.ConfigProcessor.RangeFloat;
import org.orecruncher.lib.ConfigProcessor.RangeInt;
import org.orecruncher.lib.ConfigProcessor.RestartRequired;

public final class ModOptions {
   protected static final String PREFIX = "dsurround.cfg";
   public static final String CATEGORY_ASM = "asm";
   public static final String CATEGORY_LOGGING_CONTROL = "logging";
   public static final String CATEGORY_RAIN = "rain";
   public static final String CATEGORY_FOG = "fog";
   public static final String CATEGORY_GENERAL = "general";
   public static final String CATEGORY_AURORA = "aurora";
   public static final String CATEGORY_BIOMES = "biomes";
   public static final String CATEGORY_EFFECTS = "effects";
   public static final String CATEGORY_SOUND = "sound";
   public static final String CONFIG_SOUND_SETTINGS = "Sound Settings";
   public static final String CATEGORY_PLAYER = "player";
   public static final String CATEGORY_SPEECHBUBBLES = "speechbubbles";
   public static final String CATEGORY_COMMANDS = "commands";
   public static final String CATEGORY_PROFILES = "profiles";

   public ModOptions() {
   }

   public static void load(Configuration config) {
      ConfigProcessor.process(config, ModOptions.class);
      if (ModBase.config() != null) {
         Profiles.tickle();
      }

      if (ModBase.config() != null) {
         for(String cat : config.getCategoryNames()) {
            scrubCategory(config.getCategory(cat));
         }
      }

   }

   private static void scrubCategory(ConfigCategory category) {
      List<String> killList = new ArrayList();

      for(Entry<String, Property> entry : category.entrySet()) {
         if (StringUtils.isEmpty(((Property)entry.getValue()).getComment())) {
            killList.add(entry.getKey());
         }
      }

      for(String kill : killList) {
         category.remove(kill);
      }

   }

   public static class Trace {
      public static final int SOUND_PLAY = 1;
      public static final int FOOTSTEP_ACOUSTIC = 2;
      public static final int WORLD_CAPABILITIES = 4;

      public Trace() {
      }
   }

   @Category("asm")
   @LangKey("dsurround.cfg.asm")
   @Comment("Controls ASM/Mixin transforms at startup")
   public static class asm {
      protected static final String PREFIX = "dsurround.cfg.asm";
      @Option("Enable Weather Control")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.asm.EnableWeather")
      @Comment("Enable weather rendering and handling")
      public static boolean enableWeatherASM = true;
      @Option("Disable Arrow Critical Particle Trail")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.asm.DisableArrow")
      @Comment("Disable particle trail left by an arrow when it flies")
      public static boolean disableArrowParticleTrail = true;
      @Option("Disable Potion Icons in Inventory Display")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.asm.DisablePotionIcons")
      @Comment("Disable Potion Icons in Inventory Display")
      public static boolean disablePotionIconsInInventory = false;
      @Option("Enable Search Option in Configuration")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.asm.EnableOptionSearch")
      @Comment("Enable search field in config option display")
      public static boolean enableOptionSearchASM = true;

      public asm() {
      }
   }

   @Category("aurora")
   @LangKey("dsurround.cfg.aurora")
   @Comment("Options that control Aurora behavior and rendering")
   public static class aurora {
      protected static final String PREFIX = "dsurround.cfg.aurora";
      @Option("Enabled")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.aurora.EnableAurora")
      @Comment("Enable/disable aurora processing and rendering")
      public static boolean auroraEnable = true;
      @Option("Use Shaders")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.aurora.EnableShader")
      @Comment("Use shader when rendering aurora")
      @RestartRequired
      public static boolean auroraUseShader = true;
      @Option("Maximum Bands")
      @DefaultValue("3")
      @LangKey("dsurround.cfg.aurora.MaxBands")
      @Comment("Maximum number of bands to render")
      @RangeInt(
         min = 1,
         max = 3
      )
      public static int maxBands = 3;

      public aurora() {
      }
   }

   @Category("biomes")
   @LangKey("dsurround.cfg.biomes")
   @Comment("Options for controlling biome sound/effects")
   public static class biomes {
      protected static final String PREFIX = "dsurround.cfg.biomes";
      @Option("Overworld Sealevel Override")
      @DefaultValue("0")
      @LangKey("dsurround.cfg.biomes.Sealevel")
      @RangeInt(
         min = 0,
         max = 255
      )
      @Comment("Sealevel to set for Overworld (0 use default for World)")
      public static int worldSealevelOverride = 0;
      @Option("Biomes Alias")
      @DefaultValue("")
      @LangKey("dsurround.cfg.biomes.Aliases")
      @Comment("Biomes alias list")
      public static String[] biomeAliases = new String[0];
      @Option("Dimension Blacklist")
      @DefaultValue("")
      @LangKey("dsurround.cfg.biomes.DimBlacklist")
      @Comment("Dimension IDs where biome sounds will not be played")
      public static String[] dimensionBlacklist = new String[0];

      public biomes() {
      }
   }

   @Category("commands")
   @LangKey("dsurround.cfg.commands")
   @Comment("Options for configuring commands")
   @RestartRequired(
      server = true
   )
   public static class commands {
      protected static final String PREFIX = "dsurround.cfg.commands";

      public commands() {
      }

      @Category("/calc")
      public static class calc {
         protected static final String PREFIX = "dsurround.cfg.commands.Calc";
         @Option("name")
         @DefaultValue("calc")
         @LangKey("dsurround.cfg.commands.Calc.Name")
         @Comment("Name of the command")
         public static String commandNameCalc = "calc";
         @Option("alias")
         @DefaultValue("c math")
         @LangKey("dsurround.cfg.commands.Calc.Alias")
         @Comment("Alias for the command")
         public static String commandAliasCalc = "c math";

         public calc() {
         }
      }

      @Category("/ds")
      public static class ds {
         protected static final String PREFIX = "dsurround.cfg.commands.DS";
         @Option("name")
         @DefaultValue("ds")
         @LangKey("dsurround.cfg.commands.DS.Name")
         @Comment("Name of the command")
         public static String commandNameDS = "ds";
         @Option("alias")
         @DefaultValue("dsurround rain")
         @LangKey("dsurround.cfg.commands.DS.Alias")
         @Comment("Alias for the command")
         public static String commandAliasDS = "dsurround rain";

         public ds() {
         }
      }
   }

   @Category("effects")
   @LangKey("dsurround.cfg.effects")
   @Comment("Options for controlling various effects")
   public static class effects {
      protected static final String PREFIX = "dsurround.cfg.effects";
      @Option("Special Effect Range")
      @DefaultValue("24")
      @LangKey("dsurround.cfg.effects.FXRange")
      @RangeInt(
         min = 16,
         max = 64
      )
      @Comment("Block radius/range around player for special effect application")
      public static int specialEffectRange = 24;
      @Option("Disable Water Suspend Particles")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.effects.Suspend")
      @Comment("Enable/disable water depth particle effect")
      @RestartRequired(
         server = true
      )
      public static boolean disableWaterSuspendParticle = false;
      @Option("Waterfall Cutoff")
      @DefaultValue("0")
      @LangKey("dsurround.cfg.effects.WaterfallCutoff")
      @RangeInt(
         min = 0,
         max = 16
      )
      @Comment("Waterfall strength below which sounds will not play (> 10 to turn off)")
      public static int waterfallCutoff = 0;
      @Option("Enable Steam")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Steam")
      @Comment("Enable Steam Jets where lava meets water")
      public static boolean enableSteamJets = true;
      @Option("Enable FireJetEffect Jets")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Fire")
      @Comment("Enable FireJetEffect Jets in lava")
      public static boolean enableFireJets = true;
      @Option("Enable Bubbles")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Bubble")
      @Comment("Enable BubbleJetEffect Jets under water")
      public static boolean enableBubbleJets = true;
      @Option("Enable DustJetEffect Motes")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Dust")
      @Comment("Enable DustJetEffect motes dropping from blocks")
      public static boolean enableDustJets = true;
      @Option("Enable FountainJetEffect")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Fountain")
      @Comment("Enable FountainJetEffect jets")
      public static boolean enableFountainJets = true;
      @Option("Enable Fireflies")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Fireflies")
      @Comment("Enable Firefly effect around plants")
      public static boolean enableFireflies = true;
      @Option("Enable Water Splash")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Splash")
      @Comment("Enable Water Splash effects when water spills down")
      public static boolean enableWaterSplash = true;
      @Option("Damage Popoffs")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Popoffs")
      @Comment("Controls display of damage pop-offs when an entity is damaged/healed")
      public static boolean enableDamagePopoffs = true;
      @Option("Show Crit Words")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.CritWords")
      @Comment("Display random power word on critical hit")
      public static boolean showCritWords = true;
      @Option("Footprints")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.Footprints")
      @Comment("Enable player footprints")
      public static boolean enableFootprints = true;
      @Option("Footprint Style")
      @DefaultValue("6")
      @LangKey("dsurround.cfg.effects.FootprintStyle")
      @Comment("0: shoe print, 1: square print, 2: horse hoof, 3: bird, 4: paw, 5: solid square, 6: lowres square")
      @RangeInt(
         min = 0,
         max = 6
      )
      public static int footprintStyle = 6;
      @Option("Show Frost Breath")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.effects.ShowBreath")
      @Comment("Show player breath in cold weather and underwater")
      public static boolean showBreath = true;

      public effects() {
      }
   }

   @Category("fog")
   @LangKey("dsurround.cfg.fog")
   @Comment("Options that control the various fog effects in the client")
   public static class fog {
      protected static final String PREFIX = "dsurround.cfg.fog";
      @Option("Enable Fog Processing")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.Enable")
      @Comment("Enable/disable fog processing")
      public static boolean enableFogProcessing = true;
      @Option("Morning Fog")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.EnableMorning")
      @Comment("Show morning fog that eventually burns off")
      public static boolean enableMorningFog = true;
      @Option("Weather Fog")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.EnableWeather")
      @Comment("Increase fog based on the strength of rain")
      public static boolean enableWeatherFog = true;
      @Option("Bedrock Fog")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.EnableBedrock")
      @Comment("Increase fog at bedrock layers")
      public static boolean enableBedrockFog = true;
      @Option("Desert Fog")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.DesertFog")
      @Comment("Enable/disable desert fog when raining")
      public static boolean allowDesertFog = true;
      @Option("Elevation Haze")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.ElevationHaze")
      @Comment("Higher the player elevation the more haze that is experienced")
      public static boolean enableElevationHaze = true;
      @Option("Biomes Fog")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.fog.BiomeFog")
      @Comment("Enable biome specific fog density and color")
      public static boolean enableBiomeFog = true;
      @Option("Morning Fog Chance")
      @DefaultValue("1")
      @RangeInt(
         min = 1,
         max = 10
      )
      @LangKey("dsurround.cfg.fog.MorningFogChance")
      @Comment("Chance morning fog will occurs expressed as 1 in N (1 means always)")
      public static int morningFogChance = 1;

      public fog() {
      }
   }

   @Category("general")
   @LangKey("dsurround.cfg.general")
   @Comment("Miscellaneous settings")
   public static class general {
      protected static final String PREFIX = "dsurround.cfg.general";
      @Option("External Configuration Files")
      @DefaultValue("")
      @LangKey("dsurround.cfg.general.ExternalScripts")
      @Comment("Configuration files for customization")
      public static String[] externalScriptFiles = new String[0];
      @Option("Startup Sound List")
      @DefaultValue("minecraft:entity.experience_orb.pickup,minecraft:entity.chicken.egg")
      @LangKey("dsurround.cfg.general.StartupSounds")
      @Comment("Possible sounds to play when client finishes loading and reaches the main game menu")
      public static String[] startupSoundList = new String[]{"minecraft:entity.experience_orb.pickup", "minecraft:entity.chicken.egg"};
      @Option("Enable Client Chunk Caching")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.general.ChunkCaching")
      @Comment("Enable/disable client side chunk caching (performance)")
      public static boolean enableClientChunkCaching = true;

      public general() {
      }
   }

   @Category("logging")
   @LangKey("dsurround.cfg.logging")
   @Comment("Defines how logging will behave")
   public static class logging {
      protected static final String PREFIX = "dsurround.cfg.logging";
      @Option("Enable Debug Logging")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.logging.EnableDebug")
      @Comment("Enables/disables debug log tracing")
      @RestartRequired
      public static boolean enableDebugLogging = false;
      @Option("Enable Online Version Check")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.logging.VersionCheck")
      @Comment("Enables/disables display of version check information")
      @RestartRequired
      public static boolean enableVersionChecking = true;
      @Option("Report Server Stats")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.logging.ServerStats")
      @Comment("Enables/disables reporting of server TPS and memory stats (has to be enabled server side as well)")
      public static boolean reportServerStats = false;
      @Option("Debug Flag Mask")
      @DefaultValue("0")
      @LangKey("dsurround.cfg.logging.FlagMask")
      @Comment("Bitmask for toggling various debug traces")
      @Hidden
      public static int debugFlagMask = 0;

      public logging() {
      }
   }

   @Category("player")
   @LangKey("dsurround.cfg.player")
   @Comment("General options for defining sound and effects the player entity")
   public static class player {
      protected static final String PREFIX = "dsurround.cfg.player";
      @Option("Suppress Potion Particles")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.player.PotionParticles")
      @Comment("Suppress rendering of player's potion particles")
      public static boolean suppressPotionParticles = false;
      @Option("Hurt Threshold")
      @DefaultValue("0.25")
      @LangKey("dsurround.cfg.player.HurtThreshold")
      @Comment("Percentage of player health bar remaining to trigger player hurt sound (0 disable)")
      @RangeFloat(
         min = 0.0F,
         max = 0.5F
      )
      public static float playerHurtThreshold = 0.25F;
      @Option("Hunger Threshold")
      @DefaultValue("8")
      @LangKey("dsurround.cfg.player.HungerThreshold")
      @Comment("Amount of food bar remaining to trigger player hunger sound (0 disable)")
      @RangeInt(
         min = 0,
         max = 10
      )
      public static int playerHungerThreshold = 8;

      public player() {
      }
   }

   @Category("profiles")
   @LangKey("dsurround.cfg.profiles")
   @Comment("Enable/disable application of built in profiles")
   public static class profiles {
      protected static final String PREFIX = "dsurround.cfg.profiles";

      public profiles() {
      }
   }

   @Category("rain")
   @LangKey("dsurround.cfg.rain")
   @Comment("Options that control rain effects in the client")
   public static class rain {
      protected static final String PREFIX = "dsurround.cfg.rain";
      @Option("Use Vanilla Algorithms")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.rain.VanillaRain")
      @Comment("Let Vanilla handle rain intensity and time windows")
      @RestartRequired
      public static boolean doVanillaRain = false;
      @Option("Use Vanilla Rain Sound")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.rain.UseVanillaSound")
      @Comment("Use the Vanilla rain sound rather than the modified one")
      @RestartRequired(
         server = true
      )
      public static boolean useVanillaRainSound = false;
      @Option("Style of rain water ripple")
      @DefaultValue("3")
      @LangKey("dsurround.cfg.rain.RippleStyle")
      @RangeInt(
         min = 0,
         max = 3
      )
      @Comment("0: original round, 1: darker round, 2: square, 3: pixelated")
      public static int rainRippleStyle = 3;
      @Option("Enable Background Thunder")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.rain.EnableThunder")
      @Comment("Allow background thunder when storming")
      public static boolean allowBackgroundThunder = true;
      @Option("Rain Intensity for Background Thunder")
      @DefaultValue("0.75")
      @LangKey("dsurround.cfg.rain.ThunderThreshold")
      @RangeFloat(
         min = 0.0F
      )
      @Comment("Minimum rain intensity level for background thunder to occur")
      public static float stormThunderThreshold = 0.75F;
      @Option("Default Minimum Rain Strength")
      @DefaultValue("0.0")
      @LangKey("dsurround.cfg.rain.MinRainStrength")
      @RangeFloat(
         min = 0.0F,
         max = 1.0F
      )
      @Comment("Default minimum rain strength for a dimension")
      public static float defaultMinRainStrength = 0.0F;
      @Option("Default Maximum Rain Strength")
      @DefaultValue("1.0")
      @LangKey("dsurround.cfg.rain.MaxRainStrength")
      @RangeFloat(
         min = 0.0F,
         max = 1.0F
      )
      @Comment("Default maximum rain strength for a dimension")
      public static float defaultMaxRainStrength = 1.0F;
      @Option("Enable Netherrack and Magma Splash Effect")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.rain.EnableMagmaNetherrack")
      @Comment("Enable lava particle rain splash effect on Netherrack and Magma blocks")
      public static boolean enableNetherrackMagmaSplashEffect = true;

      public rain() {
      }
   }

   @Category("sound")
   @LangKey("dsurround.cfg.sound")
   @Comment("General options for defining sound effects")
   public static class sound {
      protected static final String PREFIX = "dsurround.cfg.sound";
      @Option("Battle Music")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.sound.BattleMusic")
      @Comment("Enable/disable Battle Music (must also have BattleMusic resource pack installed to hear)")
      public static boolean enableBattleMusic = false;
      @Option("Enable Biomes Sounds")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.BiomeSounds")
      @Comment("Enable biome background and spot sounds")
      public static boolean enableBiomeSounds = true;
      @Option("Autoconfigure Channels")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.AutoConfig")
      @Comment("Automatically configure sound channels")
      @RestartRequired(
         server = true
      )
      public static boolean autoConfigureChannels = true;
      @Option("Number Normal Channels")
      @DefaultValue("28")
      @LangKey("dsurround.cfg.sound.NormalChannels")
      @RangeInt(
         min = 28,
         max = 255
      )
      @Comment("Number of normal sound channels to configure in the sound system (manual)")
      @RestartRequired(
         server = true
      )
      public static int normalSoundChannelCount = 28;
      @Option("Number Streaming Channels")
      @DefaultValue("4")
      @LangKey("dsurround.cfg.sound.StreamingChannels")
      @RangeInt(
         min = 4,
         max = 255
      )
      @Comment("Number of streaming sound channels to configure in the sound system (manual)")
      @RestartRequired(
         server = true
      )
      public static int streamingSoundChannelCount = 4;
      @Option("Stream Buffer Size")
      @DefaultValue("32")
      @LangKey("dsurround.cfg.sound.StreamBufferSize")
      @RangeInt(
         min = 0
      )
      @Comment("Size of a stream buffer in kilobytes (0: system default - usually 128K bytes)")
      @RestartRequired(
         server = true
      )
      public static int streamBufferSize = 32;
      @Option("Number of Stream Buffers per Channel")
      @DefaultValue("0")
      @LangKey("dsurround.cfg.sound.StreamBufferCount")
      @RangeInt(
         min = 0,
         max = 8
      )
      @Comment("Number of stream buffers per channel (0: system default - usually 3 buffers)")
      @RestartRequired(
         server = true
      )
      public static int streamBufferCount = 0;
      @Option("Mute when Background")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Mute")
      @Comment("Mute sound when Minecraft is in the background")
      public static boolean muteWhenBackground = true;
      @Option("Thunder Volume")
      @DefaultValue("10000")
      @LangKey("dsurround.cfg.sound.ThunderVolume")
      @Comment("Sound Volume of Thunder")
      @RangeFloat(
         min = 15.0F,
         max = 10000.0F
      )
      public static float thunderVolume = 10000.0F;
      @Option("Jump Sound")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.sound.Jump")
      @Comment("Enable player Jump sound effect")
      public static boolean enableJumpSound = false;
      @Option("Equip Sound")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Equip")
      @Comment("Enable Weapon/Tool Equip sound effect")
      public static boolean enableEquipSound = true;
      @Option("Sword Equip as Tool")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.sound.SwordEquipAsTool")
      @Comment("Enable Sword Equip sound as Tool")
      @RestartRequired(
         server = true
      )
      public static boolean swordEquipAsTool = false;
      @Option("Crafting Sound")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Craft")
      @Comment("Enable Item Crafted sound effect")
      public static boolean enableCraftingSound = true;
      @Option("Footsteps as Quadruped")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.sound.FootstepQuad")
      @Comment("Simulate quadruped with Footstep effects (horse)")
      public static boolean foostepsQuadruped = false;
      @Option("First Person Footstep Cadence")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.FootstepCadence")
      @Comment("true to match first person arm swing; false to match 3rd person leg animation")
      public static boolean firstPersonFootstepCadence = true;
      @Option("Armor Sound")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Armor")
      @Comment("Enable/disable armor sounds when moving")
      public static boolean enableArmorSounds = true;
      @Option("Swing Sound")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Swing")
      @Comment("Enable/disable item swing sounds")
      public static boolean enableSwingSounds = true;
      @Option("Rain Puddle Sound")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.sound.Puddle")
      @Comment("Enable/disable rain puddle sound when moving in the rain")
      public static boolean enablePuddleSound = true;
      @Option("Sound Culling Threshold")
      @DefaultValue("20")
      @LangKey("dsurround.cfg.sound.CullInterval")
      @RangeInt(
         min = 0
      )
      @Comment("Ticks between culled sound events (0 to disable culling)")
      public static int soundCullingThreshold = 20;
      @Option("Sound Settings")
      @Hidden
      @DefaultValue("minecraft:block.water.ambient cull,minecraft:block.lava.ambient cull,minecraft:entity.sheep.ambient cull,minecraft:entity.chicken.ambient cull,minecraft:entity.cow.ambient cull,minecraft:entity.pig.ambient cull,dsurround:bison block,dsurround:elephant block,dsurround:gnatt block,dsurround:insectbuzz block,dsurround:hiss block,dsurround:rattlesnake block")
      @LangKey("dsurround.cfg.sound.SoundSettings")
      @Comment("Configure how each individual sound will be handled (block, cull, and volume scale)")
      public static String[] soundSettings = new String[]{"minecraft:block.water.ambient cull", "minecraft:block.lava.ambient cull", "minecraft:entity.sheep.ambient cull", "minecraft:entity.chicken.ambient cull", "minecraft:entity.cow.ambient cull", "minecraft:entity.pig.ambient cull", "dsurround:bison block", "dsurround:elephant block", "dsurround:gnatt block", "dsurround:insectbuzz block", "dsurround:hiss block", "dsurround:rattlesnake block"};

      public sound() {
      }
   }

   @Category("speechbubbles")
   @LangKey("dsurround.cfg.speech")
   @Comment("Options for configuring SpeechBubbles")
   public static class speechbubbles {
      protected static final String PREFIX = "dsurround.cfg.speech";
      @Option("Enable SpeechBubbles")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.speech.EnableSpeechBubbles")
      @Comment("Enables/disables speech bubbles above player heads (needs to be enabled server side as well)")
      public static boolean enableSpeechBubbles = false;
      @Option("Enable Entity Chat")
      @DefaultValue("false")
      @LangKey("dsurround.cfg.speech.EnableEntityChat")
      @Comment("Enables/disables entity chat bubbles")
      public static boolean enableEntityChat = false;
      @Option("Display Duration")
      @DefaultValue("7")
      @LangKey("dsurround.cfg.speech.Duration")
      @RangeFloat(
         min = 5.0F,
         max = 15.0F
      )
      @Comment("Number of seconds to display speech before removing")
      public static float speechBubbleDuration = 7.0F;
      @Option("Visibility Range")
      @DefaultValue("16")
      @LangKey("dsurround.cfg.speech.Range")
      @RangeInt(
         min = 16,
         max = 32
      )
      @Comment("Range at which a SpeechBubble is visible (filtering occurs server side)")
      public static float speechBubbleRange = 16.0F;
      @Option("Animania Badges")
      @DefaultValue("true")
      @LangKey("dsurround.cfg.speech.AnimaniaBadges")
      @Comment("Enable/disable display of food/water badges over Animania mobs")
      @RestartRequired(
         server = true
      )
      public static boolean enableAnimaniaBadges = true;

      public speechbubbles() {
      }
   }
}
