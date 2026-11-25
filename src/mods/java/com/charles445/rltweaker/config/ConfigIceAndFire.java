package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigIceAndFire {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Prevent statues from picking up items (and other mob griefing events)"})
   @Name("Statue Mob Griefing Fix")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean statueMobGriefingFix = true;
   @Comment({"Prevent statues from saving some data they should not"})
   @Name("Statue Data Fixes")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean statueDataFixes = true;
   @Comment({"Interact with dragons with only the right hand"})
   @Name("Right Hand Dragon Interaction")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean rightHandDragonInteraction = true;
   @Comment({"Fixes Myrmex Queen trades that generate with the wrong type of resin"})
   @Name("Myrmex Queen Trade Fix")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean myrmexQueenTradeFix = true;
   @Comment({"Stop deathworm egg drops from being affected by looting"})
   @Name("Deathworm Eggs Ignore Looting")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean deathwormEggsIgnoreLooting = false;
   @Comment({"Prevents gorgons from targeting player statues. The presence of player statues will still give their AI trouble."})
   @Name("Stop Gorgon Targeting Player Statues")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean stopGorgonTargetingPlayerStatues = true;
   @Comment({"Removes snow trade for RLCraft balancing. Only works in Ice and Fire 1.7.1"})
   @Name("RLCraft Trade Balancing")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean rlcraftTradeBalancing = false;
   @Comment({"Requires patchAmphithereTameDamage patch! Damage that Amphitheres deal when a player tries to tame them."})
   @Name("Amphithere Tame Damage")
   @RLConfig.ImprovementsOnly("1.0")
   @RLConfig.RLCraftTwoEightTwo("1.0")
   @RLConfig.RLCraftTwoNine("1.0")
   public float amphithereTameDamage = 1.0F;

   public ConfigIceAndFire() {
   }
}
