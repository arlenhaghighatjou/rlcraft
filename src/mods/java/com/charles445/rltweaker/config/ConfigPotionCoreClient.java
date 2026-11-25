package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigPotionCoreClient {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Fix for Magic Shielding HUD Color affecting the hunger bar"})
   @Name("Magic Shielding HUD Fix")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean magicShieldingHUDFix = true;
   @Comment({"Whether to render Potion Core's armor icons on the hud"})
   @Name("Render Armor Icons")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean renderArmorIcons = true;
   @Comment({"Whether to render Potion Core's resistance red armor outlines on the hud"})
   @Name("Render Armor Resistance")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean renderArmorResistance = true;
   @Comment({"Compatibility for Overloaded Armor Bar, rendering magic shielding properly"})
   @Name("Overloaded Armor Bar Compatibility")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean overloadedArmorBarCompatibility = true;

   public ConfigPotionCoreClient() {
   }
}
