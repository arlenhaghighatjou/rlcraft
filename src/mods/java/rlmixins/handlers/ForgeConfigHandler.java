package rlmixins.handlers;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import rlmixins.RLMixins;

@Config(
   modid = "rlmixins"
)
public class ForgeConfigHandler {
   private static List<String> netherBaneMobs;
   private static List<String> netherBaneWeapons;
   @Comment({"Additional Server-Side Options"})
   @Name("Server Options")
   public static final ForgeConfigHandler.ServerConfig server = new ForgeConfigHandler.ServerConfig();
   @Comment({"Additional Client-Side Options"})
   @Name("Client Options")
   public static final ForgeConfigHandler.ClientConfig client = new ForgeConfigHandler.ClientConfig();
   @Comment({"Enable/Disable Tweaks and Patches"})
   @Name("Toggle Mixins")
   public static final ForgeConfigHandler.MixinConfig mixinConfig = new ForgeConfigHandler.MixinConfig();
   private static File configFile;
   private static String configBooleanString = "";

   public ForgeConfigHandler() {
   }

   public static List<String> getNetherBaneMobs() {
      if (netherBaneMobs == null) {
         netherBaneMobs = Arrays.asList(server.netherBaneMobs);
      }

      return netherBaneMobs;
   }

   public static List<String> getNetherBaneWeapons() {
      if (netherBaneWeapons == null) {
         netherBaneWeapons = Arrays.asList(server.netherBaneWeapons);
      }

      return netherBaneWeapons;
   }

   public static boolean getBoolean(String name) {
      if (configFile == null) {
         configFile = new File("config", "rlmixins.cfg");
         if (configFile.exists() && configFile.isFile()) {
            try {
               Stream<String> stream = Files.lines(configFile.toPath());
               Throwable var2 = null;

               try {
                  configBooleanString = (String)stream.filter((s) -> {
                     return s.trim().startsWith("B:");
                  }).collect(Collectors.joining());
               } catch (Throwable var12) {
                  var2 = var12;
                  throw var12;
               } finally {
                  if (stream != null) {
                     if (var2 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var11) {
                           var2.addSuppressed(var11);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (Exception var14) {
               RLMixins.LOGGER.log(Level.ERROR, "Failed to parse RLMixins config: " + var14);
            }
         }
      }

      return configBooleanString.contains("B:\"" + name + "\"=true");
   }

   public static class ClientConfig {
      @Comment({"Requires Spooky Dragons Mixin to be enabled"})
      @Name("Spooky Dragons")
      public boolean spookyDragons = false;

      public ClientConfig() {
      }
   }

   @EventBusSubscriber(
      modid = "rlmixins"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("rlmixins")) {
            ForgeConfigHandler.netherBaneMobs = null;
            ForgeConfigHandler.netherBaneWeapons = null;
            ConfigManager.sync("rlmixins", Type.INSTANCE);
         }

      }
   }

   public static class MixinConfig {
      @Comment({"MC-119971 patch, created by EigenCraft Unofficial Patch"})
      @Name("Outdated Chunk Data (Vanilla)")
      @RequiresMcRestart
      public boolean outdatedChunkData = false;
      @Comment({"Force Thorn and Arthropod enchantment methods to check for offhand packets, as well as Fire Aspect and Knockback with SME compat."})
      @Name("Offhand Enchants (Vanilla/RLCombat)")
      @RequiresMcRestart
      public boolean arthropodOffhandSensitivity = false;
      @Comment({"Disallow Infernal/Blight/Champion mobs from entering Minecarts and Boats (Does not require all mods to be loaded.)"})
      @Name("Boss Cart/Boat Cheese (Vanilla/InfernalMobs/ScalingHealth/Champions)")
      @RequiresMcRestart
      public boolean bossCartCheesePatch = false;
      @Comment({"Force EntityLivingBase#attemptTeleport to cancel under the effects of AntiWarp"})
      @Name("AntiWarp Handling (Vanilla/BetterSurvival)")
      @RequiresMcRestart
      public boolean antiwarpImprovement = false;
      @Comment({"Makes fall distance reduction per tick in water configurable"})
      @Name("Configurable Fall (Vanilla)")
      @RequiresMcRestart
      public boolean configurableFallDistance = false;
      @Comment({"Lower the player's eye height while crouching to be more like newer versions"})
      @Name("Lowered Crouch (Vanilla)")
      @RequiresMcRestart
      public boolean lowerEyeHeight = false;
      @Comment({"Patches issues with player health attributes being lowered between packets causing desynced death"})
      @Name("Health Desync Patch (Vanilla)")
      @RequiresMcRestart
      public boolean healthAttributePatch = false;
      @Comment({"Smooth eye height when crouching, created by RandomPatches"})
      @Name("Smoothed Crouching (Vanilla)")
      @RequiresMcRestart
      public boolean smoothCrouch = false;
      @Comment({"Force Mending to prioritize damaged items first, instead of randomly picking"})
      @Name("Mending Priorities (Vanilla)")
      @RequiresMcRestart
      public boolean mendingPriorities = false;
      @Comment({"Makes Chunk Animator stop animating around the player temporarily when using F3+A or changing render distance, to stop easy xray"})
      @Name("ChunkAnimator XRay (Vanilla/ChunkAnimator)")
      @RequiresMcRestart
      public boolean chunkAnimatorXRay = false;
      @Comment({"Stops Anvils from displaying \"Too Expensive\" for compatibility with AnvilPatchLawful"})
      @Name("Anvil Too Expensive (Vanilla/AnvilPatch)")
      @RequiresMcRestart
      public boolean anvilTooExpensiveFix = false;
      @Comment({"Directly modify Item attributes for certain SoManyEnchantments Enchantments"})
      @Name("Enchantment Item Attributes (Vanilla/SME)")
      @RequiresMcRestart
      public boolean enchantmentsModifyItemAttributes = false;
      @Comment({"Directly modify ItemStack damage for certain SoManyEnchantments Enchantments"})
      @Name("Enchantment ItemStack Damage (Vanilla/SME)")
      @RequiresMcRestart
      public boolean enchantmentsModifyItemStackDamage = false;
      @Comment({"MC-92916 patch, created by EigenCraft Unofficial Patch"})
      @Name("Entity Tracker Desync (Vanilla)")
      @RequiresMcRestart
      public boolean entityTrackerDesyncPatch = false;
      @Comment({"Fixes certain particles sent to the client from serverside never actually rendering, created by RandomPatches"})
      @Name("Missing Particle Rendering (Vanilla)")
      @RequiresMcRestart
      public boolean particleRenderPatch = false;
      @Comment({"MC-108469 patch, created by EigenCraft Unofficial Patch"})
      @Name("Chunk Entity List (Vanilla)")
      @RequiresMcRestart
      public boolean chunkEntityListUpdate = false;
      @Comment({"Overhaul a bunch of SoManyEnchantments Enchantment handlers to fix lag/bugs/offhand issues"})
      @Name("Overhaul SME (Vanilla/SME/RLCombat)")
      @RequiresMcRestart
      public boolean overhaulEnchants = false;
      @Comment({"Force gathering rain into a canteen to give purified water instead of dirty water"})
      @Name("Purified Rain Water (SimpleDifficulty)")
      @RequiresMcRestart
      public boolean purifyRainWater = false;
      @Comment({"Makes Coffee from Charm reduce the effects of Inebriation from Rustic instead of water"})
      @Name("Coffee Cures Hangover (Rustic/Charm)")
      @RequiresMcRestart
      public boolean coffeeInebriation = false;
      @Comment({"Replace the effects of Ale, Cider, and Mead with config-defined effects"})
      @Name("Config Alcohol Effects (Rustic)")
      @RequiresMcRestart
      public boolean alcoholConfig = false;
      @Comment({"Fixes Reskillable losing track of the player when returning from the end, causing baubles with level requirements to be lost"})
      @Name("Player Tracking Patch (Reskillable)")
      @RequiresMcRestart
      public boolean playerTrackingPatch = false;
      @Comment({"Allows SeedFood to bypass being locked by Reskillable (Allows eating Potatos/Carrots but not planting them)"})
      @Name("SeedFood Bypass Lock (Reskillable)")
      @RequiresMcRestart
      public boolean seedFoodBypass = false;
      @Comment({"Makes Golden Osmosis perk also repair Golden Book Wyrm armor"})
      @Name("Wyrm Osmosis (Reskillable/DefiledLands)")
      @RequiresMcRestart
      public boolean goldenBookWyrmOsmosis = false;
      @Comment({"Adds a config defined blacklist for the Hungry Farmer perk"})
      @Name("HungryFarmer Blacklist (Reskillable)")
      @RequiresMcRestart
      public boolean hungryFarmerBlacklistAbility = false;
      @Comment({"Reworks Undershirt perk to work properly with FirstAid"})
      @Name("Undershirt Rework (Reskillable/FirstAid)")
      @RequiresMcRestart
      public boolean undershirtRework = false;
      @Comment({"Patches Dupe bug with Stonelings"})
      @Name("Stoneling Dupe Patch (Quark)")
      @RequiresMcRestart
      public boolean stonelingPatch = false;
      @Comment({"Replaces Launch potion's effect from PotionCore with Delayed Launch, for compatibility with knockback effects"})
      @Name("Delayed Launch (PotionCore)")
      @RequiresMcRestart
      public boolean delayedLaunch = false;
      @Comment({"Halves the effect of Reach potion"})
      @Name("Half Reach (PotionCore)")
      @RequiresMcRestart
      public boolean halfReach = false;
      @Comment({"Modify the render bounding boxes of some Lycanite mobs to fix under/oversized render boxes"})
      @Name("Lycanite Render Box (LycanitesMobs)")
      @RequiresMcRestart
      public boolean lycaniteRenderBoxResize = false;
      @Comment({"Stops Lycanite mobs from attempting to target mobs that are stone statues, or tagged with NoAI"})
      @Name("Lycanite Targetting (LycanitesMobs/IceAndFire)")
      @RequiresMcRestart
      public boolean lycaniteTargettingPatch = false;
      @Comment({"Makes ItemPhysics use the player's reach attribute instead of a hardcoded value"})
      @Name("Item Reach Attribute (ItemPhysics)")
      @RequiresMcRestart
      public boolean itemReachAttribute = false;
      @Comment({"Makes incorrectly mixing potions in an Inspirations cauldron turn into Mundane instead of Thick potion"})
      @Name("Cauldron Failure Mundane (Inspirations)")
      @RequiresMcRestart
      public boolean inspirationsMundaneCauldron = false;
      @Comment({"Prevents Champion mobs from turning into Infernals as well"})
      @Name("No Infernal Champions (Champions/InfernalMobs)")
      @RequiresMcRestart
      public boolean preventChampionInfernals = false;
      @Comment({"Stops Infernal Mobs from spamming particles while the game is paused"})
      @Name("Infernal Particle Spam (InfernalMobs)")
      @RequiresMcRestart
      public boolean infernalParticleSpam = false;
      @Comment({"Modify the render bounding boxes of some Ice and Fire mobs to fix undersized render boxes"})
      @Name("IceAndFire Render Box (IceAndFire)")
      @RequiresMcRestart
      public boolean iceAndFireRenderBoxResize = false;
      @Comment({"Cancels Ice and Fire's multipart mob handling to allow RLCombat to handle it instead"})
      @Name("InF Multipart Handling (IceAndFire)")
      @RequiresMcRestart
      public boolean cancelIceAndFireMultipartHandling = false;
      @Comment({"Allows for reducing the amount of particles generated by dragon explosions (These normally aren't rendered without Missing Particle Rendering Patch)"})
      @Name("Explosion Particle Reduction (IceAndFire)")
      @RequiresMcRestart
      public boolean dragonParticleReduction = false;
      @Comment({"Cancels Ice and Fire's handling of weapon bonuses, allowing for RLCombat to properly handle it instead"})
      @Name("InF Bonus Handling (IceAndFire)")
      @RequiresMcRestart
      public boolean cancelIceAndFireBonusHandling = false;
      @Comment({"Fix issues with Dragon Skull, Dragon Egg, Dragon Horn, and Myrmex Egg deleting items or duping when used in offhand"})
      @Name("InF Offhand Items (IceAndFire)")
      @RequiresMcRestart
      public boolean patchIceAndFireOffhand = false;
      @Comment({"Fix Food Expansion foods deleting the entire stack when eaten if their stack size is increased"})
      @Name("Food Stack Size (FoodExpansion)")
      @RequiresMcRestart
      public boolean foodExpansionStackSizeFix = false;
      @Comment({"Fix Food Expansion's Nether Wart Soup crashing the game when eaten on a server"})
      @Name("Nether Wart Soup Crash (FoodExpansion)")
      @RequiresMcRestart
      public boolean netherWartSoupCrashFix = false;
      @Comment({"Adds the ability to define Dynamic Surroundings entity chat messages in a config file"})
      @Name("Chat Bubble Config (DSurroundings)")
      @RequiresMcRestart
      public boolean entityChatBubbleConfig = false;
      @Comment({"Tags mobs spawned from Infested Champions as summoned, allowing for Trinkets and Baubles to cancel their xp/item drops"})
      @Name("Infested Summon Tag (Champions/TrinketsAndBaubles)")
      @RequiresMcRestart
      public boolean infestedSummonTags = false;
      @Comment({"Increases the time that Jailer Champions apply the Jailed effect for, since the original mixes up seconds and ticks"})
      @Name("Jailer Champion Time (Champions)")
      @RequiresMcRestart
      public boolean jailerTimeFix = false;
      @Comment({"Replace and rework the flare gun entity and handling"})
      @Name("Flare Gun Rework (BountifulBaubles)")
      @RequiresMcRestart
      public boolean flareGunRework = false;
      @Comment({"Rework the Broken Heart trinket to work with FirstAid"})
      @Name("Broken Heart Rework (BountifulBaubles/FirstAid)")
      @RequiresMcRestart
      public boolean brokenHeartRework = false;
      @Comment({"Prevents trumpets from triggering the Gluttony amulet effect"})
      @Name("Trumpet Gluttony (BountifulBaubles/TrumpetSkeleton)")
      @RequiresMcRestart
      public boolean trumpetGluttonFix = false;
      @Comment({"Rework Obsidian Skull/Shield fire resistance handling to be less buggy"})
      @Name("Obsidian Skull/Shield Rework (BountifulBaubles)")
      @RequiresMcRestart
      public boolean obsidianResistanceRework = false;
      @Comment({"Prevents the player from removing armor cursed with Binding in the reforging station"})
      @Name("Reforging Binding Fix (BountifulBaubles)")
      @RequiresMcRestart
      public boolean reforgingBindingFix = false;
      @Comment({"Replaces SRParasites Living and Sentient armor models with custom models"})
      @Name("Replace Parasite Armor Models (SRParasites)")
      @RequiresMcRestart
      public boolean replaceParasiteArmorModel = false;
      @Comment({"Attempts to stop the ability to cheese dragons on the edge of render distance"})
      @Name("Enable AntiDragon Cheese (IceAndFire)")
      @RequiresMcRestart
      public boolean enableInFDragonCheese = false;
      @Comment({"Makes dragons bite the player for the same amount as they bite other mobs, and heal on bites"})
      @Name("Enable Better Dragon Bites (IceAndFire)")
      @RequiresMcRestart
      public boolean enableBetterDragonBites = false;
      @Comment({"Cancels SRParasites manually packet handling for reach bonuses"})
      @Name("Cancel Parasite Reach Packet (SRParasites)")
      @RequiresMcRestart
      public boolean cancelParasiteReachPacket = false;
      @Comment({"Makes Champions death messages use the Champion's name"})
      @Name("Champion Death Message Tweak (Champions)")
      @RequiresMcRestart
      public boolean championDeathMessage = false;
      @Comment({"Makes Champions with potions use invisible particles"})
      @Name("Champion Potion Invis (Champions)")
      @RequiresMcRestart
      public boolean championPotionInvis = false;
      @Comment({"Makes dragons start flying if they're stuck in water while they have a target"})
      @Name("Enable Dragon Water Flying (IceAndFire)")
      @RequiresMcRestart
      public boolean enableDragonWaterFlight = false;
      @Comment({"Stops the player from being able to dismount dragons and cyclops while they are being shaken."})
      @Name("Enable Dragon Dismount Fix (Vanilla/IceAndFire)")
      @RequiresMcRestart
      public boolean enableDragonDismountFix = false;
      @Comment({"Blacklists PotionCore Revival/1UP potion from affecting non-players, to prevent duping."})
      @Name("Prevent Revival Potion on Non-Players (PotionCore)")
      @RequiresMcRestart
      public boolean revivalPotionPlayerOnly = false;
      @Comment({"Prevents Charm Crates from being inserted into Shulker Boxes, manually and by hopper."})
      @Name("Prevent Shulker Crate Insertion (Vanilla/Charm)")
      @RequiresMcRestart
      public boolean shulkerCrateInsertion = false;
      @Comment({"Cancels Parasites attempting to run a custom spawn tick check. (Seems to help performance/spawning)"})
      @Name("Parasite Spawn Fix (SRParasites)")
      @RequiresMcRestart
      public boolean parasiteSpawnFix = false;
      @Comment({"Makes parasite spawning ignore all light level if the ignore sunlight option is true."})
      @Name("Parasite Light Level (SRParasites)")
      @RequiresMcRestart
      public boolean parasiteLightLevel = false;
      @Comment({"Makes nether portals not spawn zombie pigmen, to prevent farming."})
      @Name("Stop Pigmen Portal Spawning (Vanilla)")
      @RequiresMcRestart
      public boolean stopPigmenPortalSpawning = false;
      @Comment({"Fixes BetterNether's food bowls from deleting whole stacks when eaten."})
      @Name("Stalagnate Bowl Fix (BetterNether)")
      @RequiresMcRestart
      public boolean stalagnateBowlFix = false;
      @Comment({"Adds a blacklist to prevent certain potion effects from working on tipped arrows."})
      @Name("Tipped Arrow Blacklist (Vanilla)")
      @RequiresMcRestart
      public boolean tippedArrowBlacklist = false;
      @Comment({"Modifies the Bountiful Baubles Cobalt Shield Knockback Resistance Attribute from 10 -> 1000."})
      @Name("Cobalt Shield Increased Resistance (BountifulBaubles)")
      @RequiresMcRestart
      public boolean cobaltShieldIncreasedResistance = false;
      @Comment({"Skips checking oversized AABB for collisions when teleporting long distances, causing extreme lag."})
      @Name("EXPERIMENTAL: Teleporting Lag Patch (Vanilla)")
      @RequiresMcRestart
      public boolean teleportCollisionPatch = false;
      @Comment({"Makes feathers not passively drop from chickens if they're stoned"})
      @Name("Stoned Chicken Feather Fix (Quark/IceAndFire)")
      @RequiresMcRestart
      public boolean chickenStonedFix = false;
      @Comment({"Fixes some dupe bugs with stoned InF mobs"})
      @Name("Tamed Mob Stone Dupe (IceAndFire)")
      @RequiresMcRestart
      public boolean tameStoneDupe = false;
      @Comment({"Tweaks the values of the Education enchant."})
      @Name("Education Tweak (BetterSurvival)")
      @RequiresMcRestart
      public boolean educationTweak = false;
      @Comment({"Removes the ability to add protection enchant to shields"})
      @Name("Remove Shield Protection Enchant (Inspirations)")
      @RequiresMcRestart
      public boolean inspirationsShieldEnchantRemove = false;
      @Comment({"Attempts to fix a desync caused by ScalingHealth when a mob dies in the same tick it is spawned"})
      @Name("ScalingHealth Death Desync (ScalingHealth)")
      @RequiresMcRestart
      public boolean scalingHealthDesync = false;
      @Comment({"Forces Quark's Right-Click sign edit to sync the config value from server to client to prevent desyncs (Thanks to Venom)"})
      @Name("Sync Sign Edit Config (Quark)")
      @RequiresMcRestart
      public boolean fixRightClickSignEdit = false;
      @Comment({"Rehandles the Sentient Scythe's AOE effect to make it less ridiculous and more compatible with other effects"})
      @Name("Rehandle Sentient Scythe Effect (SRParasites/RLCombat)")
      @RequiresMcRestart
      public boolean rehandleSentientScythe = false;
      @Comment({"Makes Strays and Husks ignore whether or not they can see the sky when spawning"})
      @Name("Stray/Husk Sky Spawning Fix (Vanilla)")
      @RequiresMcRestart
      public boolean strayHuskSpawningFix = false;
      @Comment({"Makes curing Zombie Villagers count as a kill for Mob Spawner Control spawners"})
      @Name("Zombie Curing Ticks Spawners (Vanilla/MobSpawnerControl)")
      @RequiresMcRestart
      public boolean curingTicksSpawners = false;
      @Comment({"Fixes Dragonbone Bow duping arrows with the BetterSurvival Multishot enchant"})
      @Name("InF Bow Multishot patch (IceAndFire/BetterSurvival)")
      @RequiresMcRestart
      public boolean dragonboneBowMultishot = false;
      @Comment({"Allows for modifying the combo multiplier of Nunchaku"})
      @Name("Changeable Nunchaku Combo (BetterSurvival)")
      @RequiresMcRestart
      public boolean nunchakuComboModifier = false;
      @Comment({"Fixes Vampirism from Better Survival healing the player when hitting non-living entities"})
      @Name("Vampirism Cheese Patch (BetterSurvival)")
      @RequiresMcRestart
      public boolean vampirismCheese = false;
      @Comment({"Fixes Quark boat chests duping"})
      @Name("Quark Chest Boat Dupe (Quark)")
      @RequiresMcRestart
      public boolean quarkChestBoatDupe = false;
      @Comment({"Fixes multiple InF entities such as eggs and dragons randomly despawning"})
      @Name("InF Entity Despawn Patch (IceAndFire)")
      @RequiresMcRestart
      public boolean infDespawn = false;
      @Comment({"Fixes dupe issues with the Locks keyring"})
      @Name("Locks Keyring GUI Dupe Patch (Locks)")
      @RequiresMcRestart
      public boolean locksGuiDupe = false;
      @Comment({"Fixes dupe issues with the ToolBelt belt"})
      @Name("ToolBelt Belt GUI Dupe Patch (ToolBelt)")
      @RequiresMcRestart
      public boolean toolbeltGuiDupe = false;
      @Comment({"Fixes dragons being breedable when still a child or not tamed"})
      @Name("Dragon OverBreeding Patch (IceAndFire)")
      @RequiresMcRestart
      public boolean dragonBreedingPatch = false;
      @Comment({"Enabled the \"Spooky Dragons\" config option in client config"})
      @Name("Spooky Dragons Mixin (IceAndFire)")
      @RequiresMcRestart
      public boolean spookyDragonsMixin = false;
      @Comment({"Fixes not being able to use Iron Canteen and Dragon Canteen on Rain Collectors"})
      @Name("Rain Collector Canteen Fix (SimpleDifficulty)")
      @RequiresMcRestart
      public boolean canteenFix = false;
      @Comment({"Fixes the clay tool from NTP being abusable for guaranteed unbreaking enchantments"})
      @Name("Clay Tool Enchant Patch (NoTreePunching)")
      @RequiresMcRestart
      public boolean clayToolPatch = false;
      @Comment({"Fixes the mattock from NTP not breaking when tilling ground"})
      @Name("Mattock Breaking Patch (NoTreePunching)")
      @RequiresMcRestart
      public boolean mattockPatch = false;
      @Comment({"Fixes the quivers from Switchbow not closing when the quiver is dropped"})
      @Name("Switchbow Quiver Patch (Switchbow)")
      @RequiresMcRestart
      public boolean switchbowQuiverPatch = false;
      @Comment({"Fix ForgottenItems vein pickaxe mining tile entities and bypassing protection"})
      @Name("Vein Pickaxe Patch (ForgottenItems)")
      @RequiresMcRestart
      public boolean veinPickaxePatch = false;
      @Comment({"Fix Hippogrpyhs being breedable with wheat"})
      @Name("Hippogrpyh Wheat Breeding Fix (IceAndFire)")
      @RequiresMcRestart
      public boolean hippogrpyhWheatBreeding = false;
      @Comment({"Fix Reskillable cancelling indirect self damage"})
      @Name("Reskillable Indirect Self Damage Patch (Reskillable)")
      @RequiresMcRestart
      public boolean reskillableIndirectSelfDamage = false;
      @Comment({"Replace the effect from the Scarlite Sword with a config-able effect"})
      @Name("Scarlite Sword Config Effect (DefiledLands)")
      @RequiresMcRestart
      public boolean scarliteSwordConfigEffect = false;
      @Comment({"Fixes abusable Myrmex trades resulting in duping and infinite xp"})
      @Name("Myrmex Abusable Trade Dupe (IceAndFire)")
      @RequiresMcRestart
      public boolean myrmexTradeDupe = false;
      @Comment({"Fixes abusable Gorgon head using offhand swapping"})
      @Name("Gorgon Head Offhand Abuse Patch (IceAndFire)")
      @RequiresMcRestart
      public boolean gorgonHeadOffhandAbuse = false;
      @Comment({"Fixes Disenchanting table crashing when broken while someone is still in the GUI"})
      @Name("Disenchanting Table Crash Patch (Disenchanter)")
      @RequiresMcRestart
      public boolean disenchantingCrash = false;
      @Comment({"Fixes crashes caused by Quark's chat linking when items with large nbt are linked"})
      @Name("Quark Chat Link NBT Crash (Quark)")
      @RequiresMcRestart
      public boolean chatLinkCrash = false;
      @Comment({"Makes potion effects actually display their values above amplifier 3 in the inventory"})
      @Name("Potion Amplifier Visibility (Vanilla)")
      @RequiresMcRestart
      public boolean potionAmplifierVisibility = false;
      @Comment({"Makes potion effects actually display their values above amplifier 3 in dshud's display"})
      @Name("Potion Amplifier Visibility (DSHuds)")
      @RequiresMcRestart
      public boolean potionAmplifierVisibilityDSHud = false;
      @Comment({"Replaces the Parasite Cleaver effect with a config defined effect"})
      @Name("Parasite Cleaver Effect Config (SRParasites)")
      @RequiresMcRestart
      public boolean parasiteCleaverCustomEffect = false;
      @Comment({"Stops Better Foliage's enable toggle from Chunk Animator XRaying"})
      @Name("Better Foliage Chunk XRay (BetterFoliage/ChunkAnimator)")
      @RequiresMcRestart
      public boolean betterFoliageChunkXRay = false;
      @Comment({"Stops CarryOn from being able to pickup chests that have not had their loot generated"})
      @Name("CarryOn Ungenerated Chest Patch (CarryOn)")
      @RequiresMcRestart
      public boolean carryOnUngeneratedChest = false;
      @Comment({"Stops CarryOn from being able to pickup pigs that still have saddles equipped, cows and squids with cooldown, or dead entities"})
      @Name("CarryOn Pig Saddle Patch (CarryOn)")
      @RequiresMcRestart
      public boolean carryOnPigSaddlePatch = false;
      @Comment({"Fixes CorpseComplex improperly using ExperienceTotal for calculating XP returns and restoring player XP on death when it shouldnt"})
      @Name("CorpseComplex XP Death Fix (CorpseComplex)")
      @RequiresMcRestart
      public boolean corpseComplexXPDeathFix = false;
      @Comment({"Fixes Charms Magnetic enchant being janky, possible memory leaks, and also duping on SpongeForge"})
      @Name("Magnetic Dupe Patch (Charm)")
      @RequiresMcRestart
      public boolean magneticDupePatch = false;
      @Comment({"Modifies BetterSurvivals Penetration enchant to use SpartanWeaponrys penetration"})
      @Name("Penetration Fix (BetterSurvival/SpartanWeaponry/RLCombat)")
      @RequiresMcRestart
      public boolean penetrationFix = false;
      @Comment({"Fixes Flowering Oak Leaves not working properly on Dynamic Trees"})
      @Name("Flowering Oak DT Fix (DynamicTrees/BOP/DTBOP)")
      @RequiresMcRestart
      public boolean floweringOakDTFix = false;
      @Comment({"Modifies the world selection/creation buttons to clarify creating a Vanilla world or a Dregora world"})
      @Name("OTG Create World Simplify Fix (OTG)")
      @RequiresMcRestart
      public boolean otgCreateWorldFix = false;

      public MixinConfig() {
      }
   }

   public static class ServerConfig {
      @Comment({"Item Blacklist for the Hungry Farmer trait."})
      @Name("Hungry Farmer Blacklist")
      public String[] hungryFarmerBlacklist = new String[]{""};
      @Comment({"Potion Blacklist for Tipped Arrows."})
      @Name("Tipped Arrow Blacklist")
      public String[] tippedArrowBlacklist = new String[]{""};
      @Comment({"Percentage of Flame particles to ignore for Fire Dragon explosions"})
      @Name("Fire Dragon Explosion Flame Percent")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double fireDragonExplosionFlame = 0.9D;
      @Comment({"Percentage of Snow particles to ignore for Ice Dragon explosions"})
      @Name("Ice Dragon Explosion Snow Percent")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double iceDragonExplosionSnow = 0.8D;
      @Comment({"Percentage of Smoke particles to ignore for Fire Dragon explosions"})
      @Name("Fire Dragon Explosion Smoke Percent")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double fireDragonExplosionSmoke = 0.95D;
      @Comment({"Percentage of Smoke particles to ignore for Ice Dragon explosions"})
      @Name("Ice Dragon Explosion Smoke Percent")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double iceDragonExplosionSmoke = 0.95D;
      @Comment({"How many blocks to reduce fall distance by per tick in water"})
      @Name("Fall Distance Reduction in Water")
      @RangeDouble(
         min = 1.0D,
         max = 100.0D
      )
      public double fallDistanceReduction = 4.0D;
      @Comment({"Effect that drinking Ale should give"})
      @Name("Ale Effect")
      public String aleEffect = "lycanitesmobs:immunization";
      @Comment({"Effect that drinking Cider should give"})
      @Name("Cider Effect")
      public String ciderEffect = "potioncore:magic_shield";
      @Comment({"Effect that drinking Mead should give"})
      @Name("Mead Effect")
      public String meadEffect = "lycanitesmobs:rejuvenation";
      @Comment({"Register the Encumbered potion effect (Requires PotionCore)"})
      @Name("Register Encumbered")
      @RequiresMcRestart
      public boolean registerEncumbered = false;
      @Comment({"Add and register Steel armor with custom models"})
      @Name("Register Steel Armor")
      @RequiresMcRestart
      public boolean registerSteelArmor = false;
      @Comment({"Add and register Scarlite armor with custom models"})
      @Name("Register Scarlite Armor")
      @RequiresMcRestart
      public boolean registerScarliteArmor = false;
      @Comment({"Add and register Cleansing Talisman, a recipe for crafting a Curse Break book, and the Curse Break potion"})
      @Name("Register Cleansing Talisman (Charm)")
      @RequiresMcRestart
      public boolean registerCleansingTalisman = false;
      @Comment({"Register the Lesser Fire Resistance potion effect"})
      @Name("Register Lesser Fire Resistance")
      @RequiresMcRestart
      public boolean registerLesserFireResistance = false;
      @Comment({"Enables the Nether Bane weapon effect to deal bonus damage to nether mobs"})
      @Name("Enable Nether Bane (Requires RLCombat)")
      @RequiresMcRestart
      public boolean enableNetherBane = false;
      @Comment({"List of mobs to be classed as nether-mobs for the Nether Bane effect"})
      @Name("Nether Bane Mob List")
      public String[] netherBaneMobs = new String[]{"minecraft:wither_skeleton", "minecraft:zombie_pigman", "minecraft:blaze", "minecraft:magma_cube", "minecraft:wither"};
      @Comment({"List of weapons to have the Nether Bane effect"})
      @Name("Nether Bane Weapon List")
      public String[] netherBaneWeapons = new String[]{""};
      @Comment({"If true, Nether Bane effect will multiply damage, if false, additive"})
      @Name("Nether Bane Multiply/Add")
      public boolean netherBaneMultiply = false;
      @Comment({"Value to either multiply damage by or add to damage for the Nether Bane effect"})
      @Name("Nether Bane Damage Value")
      public double netherBaneValue = 4.0D;
      @Comment({"If true, Cobalt Shield will cancel knockback events, instead of only applying an attribute"})
      @Name("Cobalt Shield Cancels Knockback (Bountiful Bauble)")
      @RequiresMcRestart
      public boolean cobaltShieldCancelsKnockback = false;
      @Comment({"Fixes squid and cow milking cooldowns"})
      @Name("Milking Cooldown Fix (Inspirations)")
      @RequiresMcRestart
      public boolean milkingFix = false;
      @Comment({"Registers the Cow Potion effect"})
      @Name("Register Cow Potion")
      @RequiresMcRestart
      public boolean registerCowPotion = false;
      @Comment({"Maximum Modifier for Nunchaku (Damage * (1+this))"})
      @Name("Nunchaku Combo Max Modifier")
      public float nunchakuComboMaxModifier = 1.0F;
      @Comment({"Makes the Ice/Fire blood recipes retain enchantments and repair cost, optional compatibility for BetterSurvival (Except for Spear) and SpartanFire (You will still need to remove the original recipes with CraftTweaker.)"})
      @Name("Register Enchantment Sensitive Flame/Ice Weapon Recipes (IceAndFire)")
      @RequiresMcRestart
      public boolean registerEnchantmentSensitiveFlameIceWeapon = false;
      @Comment({"Makes Advanced Mending incompatible with Infinity (Requires SME Overhaul Mixin)"})
      @Name("Advanced Mending Infinity Restriction")
      @RequiresMcRestart
      public boolean advMendingInfinityRestrict = true;
      @Comment({"Makes Advanced Mending prioritize damaged items first (Requires SME Overhaul Mixin)"})
      @Name("Advanced Mending Damaged Priority")
      @RequiresMcRestart
      public boolean advMendingDamagedPriority = true;
      @Comment({"Effect given by Scarlite Sword when hitting an entity."})
      @Name("Scarlite Sword Effect")
      public String scarliteSwordEffect = "lycanitesmobs:leech";
      @Comment({"Duration of effect given by Scarlite Sword when hitting an entity"})
      @Name("Scarlite Sword Effect Duration")
      @RangeInt(
         min = 1,
         max = 1200
      )
      public int scarliteSwordDuration = 20;
      @Comment({"Amplifier of effect given by Scarlite Sword when hitting an entity"})
      @Name("Scarlite Sword Effect Amplifier")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int scarliteSwordAmplifier = 1;
      @Comment({"Effect given by Parasite Cleaver when hitting an entity."})
      @Name("Parasite Cleaver Effect")
      public String parasiteCleaverEffect = "potioncore:vulnerable";
      @Comment({"Amplifier given by Living Cleaver when hitting an entity."})
      @Name("Living Cleaver Amplifier")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int livingCleaverAmplifier = 0;
      @Comment({"Amplifier given by Sentient Cleaver when hitting an entity."})
      @Name("Sentient Cleaver Amplifier")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int sentientCleaverAmplifier = 1;
      @Comment({"Makes Parasite armor cure/lower the effect of Fear"})
      @Name("Parasite Armor Fear Curing")
      @RequiresMcRestart
      public boolean parasiteArmorFearCuring = false;
      @Comment({"Makes Parasite armor cure/lower the effect of Viral"})
      @Name("Parasite Armor Viral Curing")
      @RequiresMcRestart
      public boolean parasiteArmorViralCuring = false;
      @Comment({"Maximum amplifier of Fear while wearing Parasite armor (-1 = cures it)"})
      @Name("Parasite Armor Fear Max Amplifier")
      @RangeInt(
         min = -1,
         max = 10
      )
      public int parasiteArmorFearMax = 1;
      @Comment({"Maximum amplifier of Viral while wearing Parasite armor (-1 = cures it)"})
      @Name("Parasite Armor Viral Max Amplifier")
      @RangeInt(
         min = -1,
         max = 10
      )
      public int parasiteArmorViralMax = 2;
      @Comment({"Whether or not Atomic Deconstructor should attempt to replicate /kill damage (More effective, but may cause bugs)"})
      @Name("Atomic Deconstructor /kill")
      public boolean atomicDeconstructorMaxDamage = false;
      @Comment({"Whether or not Atomic Deconstructor should work on bosses (May cause bugs)"})
      @Name("Atomic Deconstructor Works on Bosses")
      public boolean atomicDeconstructorBosses = false;

      public ServerConfig() {
      }
   }
}
