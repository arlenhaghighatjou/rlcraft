package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigCharm {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Fix enchantments appearing on incompatible items"})
   @Name("Fix Incorrect Item Enchantments")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixIncorrectItemEnchantments = true;
   @Comment({"Fix Salvage not being removed from villager trades when disabled"})
   @Name("Fix Salvage Trade")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixSalvageTrade = true;
   @Comment({"Fix charged emeralds crashing when being fired from a dispenser"})
   @Name("Fix Charged Emerald Crash")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixChargedEmeraldCrash = true;
   @Comment({"Disables the effect of Magnetic without unregistering it to avoid packet issues. Do this if you experience dupes with the enchantment."})
   @Name("Disable Magnetic Enchantment")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean disableMagneticEnchantment = false;

   public ConfigCharm() {
   }
}
