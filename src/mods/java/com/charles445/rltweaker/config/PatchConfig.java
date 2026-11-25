package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import com.charles445.rltweaker.config.annotation.SpecialEnum;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class PatchConfig {
   @RequiresMcRestart
   @Comment({"Master switch for the coremod"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean ENABLED = true;
   @RequiresMcRestart
   @Comment({"Enable to write modified classes to disk."})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean export = false;
   @RequiresMcRestart
   @Comment({"Makes the particle queue threaded. Fixes concurrency issue with logical server creating physical client particles."})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean particleThreading = true;
   @RequiresMcRestart
   @Comment({"Makes some entities stop checking for large entity collisions. Not needed without a max entity radius changing mod."})
   @RLConfig.SpecialSignature(
      value = SpecialEnum.MAX_ENTITY_RADIUS_HIGH,
      pass = "true",
      fail = "false"
   )
   public boolean lessCollisions = true;
   @RequiresMcRestart
   @Comment({"Allows for mounted combat with BetterCombat"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean betterCombatMountFix = true;
   @RequiresMcRestart
   @Comment({"Fixes RealBench dupe bug"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean realBenchDupeBugFix = true;
   @RequiresMcRestart
   @Comment({"Fixes Myrmex Queen hive spam"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean iafFixMyrmexQueenHiveSpam = true;
   @RequiresMcRestart
   @Comment({"Fixes Lycanites Pet Dupe in older LycanitesMobs versions than 2.0.8.0, may cause crashes in newer versions."})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean lycanitesPetDupeFix = false;
   @RequiresMcRestart
   @Comment({"Fixes mobs having trouble pathing through open doors"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean doorPathfindingFix = true;
   @RequiresMcRestart
   @Comment({"Reduces search size for finding some entities like players and items. Not needed without a max entity radius changing mod. Helps with Quark Monster Box lag."})
   @RLConfig.SpecialSignature(
      value = SpecialEnum.MAX_ENTITY_RADIUS_HIGH,
      pass = "true",
      fail = "false"
   )
   public boolean reducedSearchSize = false;
   @RequiresMcRestart
   @Comment({"Enables config option to tweak broadcasted sounds."})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchBroadcastSounds = false;
   @RequiresMcRestart
   @Comment({"Enables config option to blacklist enchantments."})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchEnchantments = false;
   @RequiresMcRestart
   @Comment({"Makes the motion checker even more aggressive."})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean aggressiveMotionChecker = true;
   @RequiresMcRestart
   @Comment({"Enables Entity Block Destroy Blacklist"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchEntityBlockDestroy = false;
   @RequiresMcRestart
   @Comment({"Patches a dupe with modded item frames, specifically Quark"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchItemFrameDupe = true;
   @RequiresMcRestart
   @Comment({"Enables Entity Push Prevention"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchPushReaction = false;
   @RequiresMcRestart
   @Comment({"Fixes some mod related anvil dupes"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true)")
   public boolean patchAnvilDupe = true;
   @RequiresMcRestart
   @Comment({"Allows for client side overlay text configuration"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchOverlayMessage = false;
   @RequiresMcRestart
   @Comment({"Allows for some hopper tweaks"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchHopper = false;
   @RequiresMcRestart
   @Comment({"Allows for critical events with BetterCombat"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean betterCombatCriticalsFix = true;
   @RequiresMcRestart
   @Comment({"Fixes hippocampus issues in Ice and Fire 1.7.1, do not enable this patch for other versions!"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixOldHippocampus = false;
   @RequiresMcRestart
   @Comment({"Fixes gorgon issues in Ice and Fire 1.7.1, do not enable this patch for other versions!"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixOldGorgon = false;
   @RequiresMcRestart
   @Comment({"Fixes crash with bound scrolls and return scrolls, and removes their unexpected spawn setting"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixWaystoneScrolls = true;
   @RequiresMcRestart
   @Comment({"Fixes ghost chunkloading when creating pathfinding chunk caches"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean pathfindingChunkCacheFix = true;
   @RequiresMcRestart
   @Comment({"Attempt to be compatible with alternative server software"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean serverCompatibility = true;
   @RequiresMcRestart
   @Comment({"Enables features for ticking chunk blocks"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean chunkTicks = true;
   @RequiresMcRestart
   @Comment({"Enables config options to tweak what effects are incurable"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchCurePotion = true;
   @RequiresMcRestart
   @Comment({"Enable to make wine potion effect modifications configurable through RLTweaker"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchRusticWineEffects = true;
   @RequiresMcRestart
   @Comment({"Fixes modifier books from BountifulBaubles changing the original item when put into an anvil"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchModifierBooks = true;
   @RequiresMcRestart
   @Comment({"Fixes Chest of Drawers from BetterNether not dropping its content when broken"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchChestOfDrawers = true;
   @RequiresMcRestart
   @Comment({"Enables config option to tweak amphithere tame damage"})
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean patchAmphithereTameDamage = false;
   @RequiresMcRestart
   @Comment({"Requires patchCustomAttributeInstances patch! Fixes Potion Core Resistance calculations"})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchPotionCoreResistance = true;
   @RequiresMcRestart
   @Comment({"Enables custom attribute instances. Required by other patches."})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchCustomAttributeInstances = true;
   @RequiresMcRestart
   @Comment({"Fixes falling block portal dupe."})
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean patchFallingBlockPortalDupe = true;

   public PatchConfig() {
   }
}
