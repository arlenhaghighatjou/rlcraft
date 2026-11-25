package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigMinecraft {
   @Comment({"Debug mode, sends messages to all players and otherwise spams, do not enable unless you are privately testing!"})
   @Name("Debug Mode")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean debug = false;
   @Comment({"Aggressively checks for invalid living entity movement and attempts to fix it"})
   @Name("Motion Checker")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean motionChecker = true;
   @Comment({"Speed cap for the motion checker, living entities are not allowed to move faster than this"})
   @Name("Motion Checker Speed Cap")
   @RangeDouble(
      min = 1.0D
   )
   @RLConfig.ImprovementsOnly("96.0")
   @RLConfig.RLCraftTwoEightTwo("48.0")
   @RLConfig.RLCraftTwoNine("96.0")
   public double motionCheckerSpeedCap = 96.0D;
   @Comment({"Synchronizes dismounts with players more aggressively"})
   @Name("Player Dismount Sync")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean playerDismountSync = true;
   @Comment({"Synchronizes arrows with players more aggressively"})
   @Name("Player Arrow Sync")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean playerArrowSync = true;
   @Comment({"Removes some blacksmith chest loot to match TAN"})
   @Name("Blacksmith Chest Tweak")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean blacksmithChestTweak = false;
   @Comment({"In Minecraft 1.2.5, players who get knocked back have their camera tilted in the direction of the attack."})
   @Name("Damage Tilt Effect")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean damageTilt = false;
   @Comment({"Requires lessCollisions patch, change that config value instead, don't change this unless you are benchmarking"})
   @Name("Less Collisions")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean lessCollisions = true;
   @Comment({"Cleans up Mineshaft .dat files regularly to lower RAM usage. May break mods that need to locate Mineshafts."})
   @Name("Cleanup Mineshaft Worldgen Files")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean cleanupMineshaftWorldgenFiles = true;
   @Comment({"Replace thrown witch potions with configured potions"})
   @Name("Witch Potion Replacements")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean witchPotionReplacements = false;
   @Comment({"Replace thrown witch harming potions with configured potions"})
   @Name("Witch Potion Replacements - Harming")
   @RLConfig.ImprovementsOnly("minecraft:harming")
   @RLConfig.RLCraftTwoEightTwo("minecraft:harming")
   @RLConfig.RLCraftTwoNine("minecraft:harming")
   public String[] witchHarmingReplacements = new String[]{"minecraft:harming"};
   @Comment({"Replace thrown witch slowness potions with configured potions"})
   @Name("Witch Potion Replacements - Slowness")
   @RLConfig.ImprovementsOnly("minecraft:slowness")
   @RLConfig.RLCraftTwoEightTwo("minecraft:slowness")
   @RLConfig.RLCraftTwoNine("minecraft:slowness")
   public String[] witchSlownessReplacements = new String[]{"minecraft:slowness"};
   @Comment({"Replace thrown witch poison potions with configured potions"})
   @Name("Witch Potion Replacements - Poison")
   @RLConfig.ImprovementsOnly("minecraft:poison")
   @RLConfig.RLCraftTwoEightTwo("minecraft:poison")
   @RLConfig.RLCraftTwoNine("minecraft:poison")
   public String[] witchPoisonReplacements = new String[]{"minecraft:poison"};
   @Comment({"Replace thrown witch weakness potions with configured potions"})
   @Name("Witch Potion Replacements - Weakness")
   @RLConfig.ImprovementsOnly("minecraft:weakness")
   @RLConfig.RLCraftTwoEightTwo("minecraft:weakness")
   @RLConfig.RLCraftTwoNine("minecraft:weakness")
   public String[] witchWeaknessReplacements = new String[]{"minecraft:weakness"};
   @Comment({"Allows all zombies to break doors"})
   @Name("All Zombies Break Doors")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean allZombiesBreakDoors = false;
   @Comment({"Distance in chunks lightning can be heard by a player. Default setting disables this tweak"})
   @Name("Lightning Sound Chunk Distance")
   @RangeDouble(
      min = 2.0D,
      max = 3000000.0D
   )
   @RLConfig.ImprovementsOnly("10000.0")
   @RLConfig.RLCraftTwoEightTwo("10000.0")
   @RLConfig.RLCraftTwoNine("100.0")
   public double lightningSoundChunkDistance = 10000.0D;
   @Comment({"REQUIRES Patch patchBroadcastSounds. Globally broadcasted sounds are only sent to players this close by, in blocks. Ignores dimension."})
   @Name("Broadcasted Sounds Distance Limit")
   @RangeDouble(
      min = 2.0D,
      max = 4.0E7D
   )
   @RLConfig.ImprovementsOnly("1000.0")
   @RLConfig.RLCraftTwoEightTwo("1000.0")
   @RLConfig.RLCraftTwoNine("1000.0")
   public double broadcastedSoundsDistanceLimit = 1000.0D;
   @Comment({"REQUIRES Patch patchEnchantments. Blacklisted enchantments do not show up as random enchantments. May still show up via other mods."})
   @Name("Enchantment Blacklist")
   @RLConfig.ImprovementsOnly("examplemod:enchantment")
   @RLConfig.RLCraftTwoEightTwo("examplemod:enchantment")
   @RLConfig.RLCraftTwoNine("examplemod:enchantment")
   public String[] blacklistedEnchantments = new String[]{"examplemod:enchantment"};
   @Comment({"Container class names to enforce player distance (to prevent dupes and other glitchy behavior). Must be the full qualified class name of the containers."})
   @Name("Container Distance Classes")
   @RLConfig.ImprovementsOnly("examplemod.container.ExampleContainer")
   @RLConfig.RLCraftTwoEightTwo("examplemod.container.ExampleContainer")
   @RLConfig.RLCraftTwoNine("examplemod.container.ExampleContainer")
   public String[] containerDistanceClasses = new String[]{"examplemod.container.ExampleContainer"};
   @Comment({"REQUIRES patch patchEntityBlockDestroy. Prevents entities from destroying these blocks."})
   @Name("Entity Block Destroy Blacklist")
   @RLConfig.ImprovementsOnly("examplemod:block")
   @RLConfig.RLCraftTwoEightTwo("examplemod:block")
   @RLConfig.RLCraftTwoNine("bountiful:bountyboard|waystones:waystone")
   public String[] entityBlockDestroyBlacklist = new String[]{"examplemod:block"};
   @Comment({"REQUIRES patch patchPushReaction. Prevents specified entities from being pushed by pistons."})
   @Name("Entity Push Prevention")
   @RLConfig.ImprovementsOnly("examplemod:entity")
   @RLConfig.RLCraftTwoEightTwo("examplemod:entity")
   @RLConfig.RLCraftTwoNine("battletowers:golem")
   public String[] entityPushPrevention = new String[]{"examplemod:entity"};
   @Comment({"REQUIRES patch patchHopper. Prevents hoppers from pulling from or inserting into specific blocks."})
   @Name("Hopper Block Blacklist")
   @RLConfig.ImprovementsOnly("examplemod:block")
   @RLConfig.RLCraftTwoEightTwo("examplemod:block")
   @RLConfig.RLCraftTwoNine("bountiful:bountyboard")
   public String[] hopperBlockBlacklist = new String[]{"examplemod:block"};
   @Comment({"Whether lightning should destroy items"})
   @Name("Lightning Destroys Items")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean lightningDestroysItems = true;
   @Comment({"Enables watchdog related features"})
   @Name("Watchdog")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean watchdog = true;
   @Comment({"Enables investigate AI for all entities defined in the 'investigateAI.json' file."})
   @Name("Investigate AI")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean investigateAi = false;
   @Comment({"Enables investigate AI for non-player attacks. Might cause lags."})
   @Name("Investigate non-player Attacks")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean investigateNonPlayerAttacks = false;
   @Comment({"Enables investigate AI for non-player attacks. Might cause lags."})
   @Name("Investigate non-player Attacks")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixDropDisconnectLagDupe = true;

   public ConfigMinecraft() {
   }
}
